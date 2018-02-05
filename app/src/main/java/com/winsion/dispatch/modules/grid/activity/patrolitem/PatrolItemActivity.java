package com.winsion.dispatch.modules.grid.activity.patrolitem;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.winsion.dispatch.R;
import com.winsion.dispatch.base.BaseActivity;
import com.winsion.dispatch.modules.grid.activity.submitproblem.SubmitProblemActivity;
import com.winsion.dispatch.modules.grid.adapter.PatrolItemAdapter;
import com.winsion.dispatch.modules.grid.constants.DeviceState;
import com.winsion.dispatch.modules.grid.constants.PatrolItemState;
import com.winsion.dispatch.modules.grid.entity.PatrolItemEntity;
import com.winsion.dispatch.modules.grid.entity.PatrolPlanEntity;
import com.winsion.dispatch.utils.ConvertUtils;
import com.winsion.dispatch.utils.constants.Formatter;
import com.winsion.dispatch.view.TitleView;

import org.greenrobot.eventbus.EventBus;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by 10295 on 2018/2/1.
 * 巡检项界面
 */

public class PatrolItemActivity extends BaseActivity implements PatrolItemContract.View, PatrolItemAdapter.Operator {
    @BindView(R.id.tv_title)
    TitleView tvTitle;
    @BindView(R.id.tv_arrive_time)
    TextView tvArriveTime;
    @BindView(R.id.tv_finish_time)
    TextView tvFinishTime;
    @BindView(R.id.lv_list)
    ListView lvList;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.fl_container)
    FrameLayout flContainer;

    public static final String PATROL_TASK_ENTITY = "patrolPlanEntity";

    // 上个页面带过来的
    private PatrolPlanEntity patrolPlanEntity;
    private PatrolItemContract.Presenter mPresenter;
    private List<PatrolItemEntity> listData = new ArrayList<>();
    private PatrolItemAdapter mAdapter;
    // 跳转设备报修界面时带过去
    private String devicePatrolDetailId;

    @Override
    protected int setContentView() {
        return R.layout.activity_patrol_item;
    }

    @Override
    protected void start() {
        initPresenter();
        initIntentData();
        initViewData();
        initListener();
        initAdapter();
        initPatrolItemData();
    }

    private void initPresenter() {
        mPresenter = new PatrolItemPresenter(this);
        mPresenter.start();
    }

    private void initIntentData() {
        patrolPlanEntity = (PatrolPlanEntity) getIntent().getSerializableExtra(PATROL_TASK_ENTITY);
        if (patrolPlanEntity == null) throw new InvalidParameterException();
    }

    private void initViewData() {
        // 到位时间
        String realStartTime = patrolPlanEntity.getRealstarttime();
        if (!equals(realStartTime, "-- --")) {
            tvArriveTime.setText(realStartTime.split(" ")[1].substring(0, 5));
        } else {
            tvArriveTime.setText("--:--");
        }

        // 完成时间
        String realEndTime = patrolPlanEntity.getRealendtime();
        if (!equals(realEndTime, "-- --")) {
            tvFinishTime.setText(realEndTime.split(" ")[1].substring(0, 5));
        } else {
            tvFinishTime.setText("--:--");
        }
    }

    private void initListener() {
        tvTitle.setOnBackClickListener(v -> finish());
        tvTitle.setOnConfirmClickListener(v -> {
            if (isEmpty(devicePatrolDetailId)) {
                showToast("需要先获取巡视项目");
            } else {
                // 设备报修
                Intent intent = new Intent(mContext, SubmitProblemActivity.class);
                intent.putExtra(SubmitProblemActivity.PATROL_DETAIL_ID, devicePatrolDetailId);
                intent.putExtra(SubmitProblemActivity.SITE_NAME, patrolPlanEntity.getPointname());
                intent.putExtra(SubmitProblemActivity.DEVICE_DEPENDENT, true);
                startActivityForResult(intent, 200);
            }
        });
        swipeRefresh.setColorSchemeResources(R.color.blue1);
        swipeRefresh.setOnRefreshListener(() -> mPresenter.getPatrolItemData(patrolPlanEntity.getId()));
    }

    private void initAdapter() {
        mAdapter = new PatrolItemAdapter(mContext, listData);
        mAdapter.setOperator(this);
        lvList.setAdapter(mAdapter);
    }

    @Override
    public void onNormalClick(PatrolItemEntity patrolItemEntity) {
        if (equals(patrolItemEntity.getDevicestate(), PatrolItemState.UNDONE)) {
            mPresenter.submitProblemWithoutDevice(patrolItemEntity, DeviceState.WORK);
        }
    }

    @Override
    public void onAbnormalClick(PatrolItemEntity patrolItemEntity) {
        if (equals(patrolItemEntity.getDevicestate(), PatrolItemState.UNDONE)) {
            new AlertDialog.Builder(mContext)
                    .setMessage(R.string.do_you_want_to_add_problem_desc)
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                        // 取消，直接上报问题
                        mPresenter.submitProblemWithoutDevice(patrolItemEntity, DeviceState.FAILURE);
                    })
                    .setPositiveButton(R.string.confirm, (dialog, which) -> {
                        Intent intent = new Intent(mContext, SubmitProblemActivity.class);
                        intent.putExtra(SubmitProblemActivity.PATROL_DETAIL_ID, patrolItemEntity.getId());
                        intent.putExtra(SubmitProblemActivity.SITE_NAME, patrolPlanEntity.getPointname());
                        intent.putExtra(SubmitProblemActivity.DEVICE_DEPENDENT, false);
                        startActivityForResult(intent, 200);
                    })
                    .create()
                    .show();
        }
    }

    @Override
    public void problemStateChangeSuccess(PatrolItemEntity patrolItemEntity, String deviceState) {
        String currentTime = ConvertUtils.formatDate(System.currentTimeMillis(), Formatter.DATE_FORMAT1);
        // 检查是否需要设置到位时间和完成时间
        if (isFirstItemFinish()) {
            patrolPlanEntity.setRealstarttime(currentTime);
            initViewData();
        }
        if (isLastItemFinish()) {
            patrolPlanEntity.setRealendtime(currentTime);
            initViewData();
        }

        // 设置巡检项操作后状态和操作时间
        patrolItemEntity.setDevicestate(equals(deviceState, DeviceState.WORK) ? PatrolItemState.NORMAL : PatrolItemState.ABNORMAL);
        patrolItemEntity.setPatroltime(currentTime);
        mAdapter.notifyDataSetChanged();

        // 已完成巡检项数量+1
        int finishCount = patrolPlanEntity.getFinishcount();
        patrolPlanEntity.setFinishcount(++finishCount);

        // 发送广播通知上个界面(PatrolPlanFragment)同步状态
        EventBus.getDefault().post(patrolPlanEntity);
    }

    private boolean isLastItemFinish() {
        int count = 0;
        for (PatrolItemEntity patrolItemDto : listData) {
            if (equals(patrolItemDto.getDevicestate(), PatrolItemState.UNDONE)) {
                count++;
            }
        }
        return count == 1;
    }

    private boolean isFirstItemFinish() {
        int count = 0;
        for (PatrolItemEntity patrolItemDto : listData) {
            if (!equals(patrolItemDto.getDevicestate(), PatrolItemState.UNDONE)) {
                count++;
            }
        }
        return count == 0;
    }

    @Override
    public void problemStateChangeFailed() {
        showToast(R.string.submit_failed);
    }

    private void initPatrolItemData() {
        mPresenter.getPatrolItemData(patrolPlanEntity.getId());
    }

    @Override
    public void getPatrolItemDataSuccess(List<PatrolItemEntity> patrolItemEntities) {
        listData.clear();
        swipeRefresh.setRefreshing(false);
        if (patrolItemEntities.size() == 0) {
            devicePatrolDetailId = null;
            tvHint.setText(R.string.no_data_click_to_retry);
            showView(flContainer, tvHint);
        } else {
            for (PatrolItemEntity patrolItemDto : patrolItemEntities) {
                if (equals(patrolItemDto.getItemdescribe(), "设备检查")) {
                    devicePatrolDetailId = patrolItemDto.getId();
                } else {
                    listData.add(patrolItemDto);
                }
            }
            mAdapter.notifyDataSetChanged();
            showView(flContainer, swipeRefresh);
        }
    }

    @Override
    public void getPatrolItemDataFailed() {
        listData.clear();
        devicePatrolDetailId = null;
        swipeRefresh.setRefreshing(false);
        tvHint.setText(R.string.failure_load_click_retry);
        showView(flContainer, tvHint);
    }

    @OnClick(R.id.tv_hint)
    public void onViewClicked() {
        showView(flContainer, progressBar);
        mPresenter.getPatrolItemData(patrolPlanEntity.getId());
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.exit();
    }
}
