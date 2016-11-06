package cn.life3t.life3t.cleaning;

import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

import cn.life3t.life3t.MyPrefs_;
import cn.life3t.life3t.R;
import cn.life3t.life3t.account.AddressManagementActivity_;
import cn.life3t.life3t.common.ActionSheetDialog;
import cn.life3t.life3t.common.CommonDialog;
import cn.life3t.life3t.common.CommonTitle;
import cn.life3t.life3t.common.ImageEditNumericView;
import cn.life3t.life3t.common.ImageEditView;
import cn.life3t.life3t.common.ImageTextArrowView;
import cn.life3t.life3t.common.NumericTextView;
import cn.life3t.life3t.common.ServiceTimePicker;
import cn.life3t.life3t.common.ServiceTimeSelectionView;
import cn.life3t.life3t.common.StaffSelectView;
import cn.life3t.life3t.main.BaseActivity;
import cn.life3t.life3t.message.AddressInfo;
import cn.life3t.life3t.message.Schedule;
import cn.life3t.life3t.message.ServiceTimeItem;
import cn.life3t.life3t.message.ServiceTimeSelectionItem;
import cn.life3t.life3t.message.StaffInfo;
import cn.life3t.life3t.message.StaffRequestInfo;
import cn.life3t.life3t.message.request.CreateFloorCleanOrderRequest;
import cn.life3t.life3t.message.request.CreateHoodCleanOrderRequest;
import cn.life3t.life3t.message.request.CreateNormalCleanOrderRequest;
import cn.life3t.life3t.message.request.CreateSofaCleanOrderRequest;
import cn.life3t.life3t.message.response.AddressListResponse;
import cn.life3t.life3t.message.response.BaseResponse;
import cn.life3t.life3t.message.response.CreateOrderResponse;
import cn.life3t.life3t.message.response.ScheduleResponse;
import cn.life3t.life3t.message.type.OrderType;
import cn.life3t.life3t.order.OrderDetailActivity_;
import cn.life3t.life3t.utils.CommonUtils;
import cn.life3t.life3t.utils.Consts;

/**
 * Created by Lei on 2015/5/21.
 */
@EActivity(R.layout.activity_normal_cleaning_service_order_submit)
public class CleaningServiceOrderSubmitActivity extends BaseActivity implements NumericTextView.NumberChangedListener {
    private static final int REQUEST_CODE_CHOOSE_ADDRESS = 1;
    private static final int REQUEST_CODE_CHOOSE_STAFF = 2;

    AddressInfo _addressSelected;
    String _timeSelected;
    String _dateSelected;
    private int _lastDateIndexSelected = 0;
    private int _lastTimeIndexSelected = 0;
    int _durationSelected = Consts.VALUE_MIN_DURATION;  //分钟
    int _countSelected = Consts.VALUE_MIN_STAFF;
    List<Schedule> _schedules;

    @Pref
    MyPrefs_ _prefs;

    @Extra
    int _serviceType = OrderType.NORMAL;

    @ViewById(R.id.common_title)
    CommonTitle _titleView;

    @ViewById(R.id.text_price_info)
    TextView _priceInfoView;

    @ViewById(R.id.address)
    ImageTextArrowView _addressView;

    @ViewById(R.id.time)
    ImageTextArrowView _timeView;

    ServiceTimePicker _timePicker;

    @ViewById(R.id.count)
    ImageEditNumericView _countView;

    @ViewById(R.id.staffSelect)
    StaffSelectView _staffSelectView;

    @ViewById(R.id.phone)
    ImageEditView _phoneView;

    @ViewById(R.id.comment)
    ImageEditView _commentView;

    @Click(R.id.price_info)
    void onPriceInfo() {
//        CleaningServiceInfoActivity_.intent(this).start();
        SpecifiedServiceInfoActivity_.intent(this)._serviceType(_serviceType).start();
    }

    @Click(R.id.address)
    void onChangeAddress() {
        int addressSelectedId = -1;
        if (_addressSelected != null)
            addressSelectedId = _addressSelected.id;
        AddressManagementActivity_.intent(this).mChooseAddress(true).mAddressSelectedId(addressSelectedId).startForResult(REQUEST_CODE_CHOOSE_ADDRESS);
    }

    @Click(R.id.time)
    void onChooseTime() {
        if (_addressSelected == null) {
            _activityHelper.showToast(getString(R.string.error_address_empty));
            return;
        }

        showTimeDialog();
    }

    @Click(R.id.submit)
    void onSubmit() {
        if (_addressSelected == null) {
            _activityHelper.showToast(getString(R.string.error_address_empty));
            return;
        }
        if (_timeSelected == null) {
            _activityHelper.showToast(getString(R.string.error_time_empty));
            return;
        }

        String contact = _phoneView.getContentText();
        if (contact.isEmpty()) {
            _phoneView.requestFocus();
            _activityHelper.showToast(getString(R.string.error_phone_empty));
            return;
        }

        if (!CommonUtils.isValidPhoneNumber(contact) && !CommonUtils.isValidTelNumber(contact)) {
            _phoneView.requestFocus();
            _activityHelper.showToast(getString(R.string.error_phone_format));
            return;
        }

        final CommonDialog dialog = new CommonDialog(this)
                .builder()
                .setContent(getString(R.string.confirm_submit_order));
        dialog.setOnCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnConfirmClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                submitOrder();
            }
        });
        dialog.show();
    }

    private void clearTimeSelection() {
        _timeSelected = null;
        _timeView.setContentText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CHOOSE_ADDRESS) {
                if (data.hasExtra("address")) {
                    AddressInfo address = (AddressInfo) data.getSerializableExtra("address");
                    _addressView.setContentText(address.addressMain+address.addressSub);
                    _addressSelected = address;
                    if (!data.hasExtra("same")) {
                        clearTimeSelection();
                        clearStaffSelected();
                        updateStaffSelectView();
                    }
                }
                if (data.hasExtra("delete")) {
                    _addressSelected = null;
                    _addressView.setContentText("");
                    clearTimeSelection();
                    clearStaffSelected();
                    updateStaffSelectView();
                }
            }
            else if (requestCode == REQUEST_CODE_CHOOSE_STAFF) {
                Object result = data.getSerializableExtra("staff");
                if (result != null) {
                    clearStaffSelected();
                    Object[] array = (Object[])result;
                    for (Object staff : array) {
                        _staffSelectView.addStaff((StaffInfo)staff);
                    }
                }
            }
        }
    }

    @AfterViews
    void afterViews() {
        String phone = _prefs.account().get();
        _phoneView.setInputType(InputType.TYPE_CLASS_PHONE);
        _phoneView.setContentText(phone);

        _staffSelectView.setTargetCount(_countSelected);

        switch (_serviceType) {
            case OrderType.NORMAL:
                _titleView.setTitle(getString(R.string.cleaning_service_normal));
                _priceInfoView.setText(R.string.price_normal_clean);
                break;
            case OrderType.DEEP:
                _titleView.setTitle(getString(R.string.cleaning_service_deep));
                _priceInfoView.setText(R.string.price_deep_clean);
                break;
            case OrderType.FLOOR:
                _titleView.setTitle(getString(R.string.cleaning_service_floor));
                _priceInfoView.setText(R.string.price_floor);
                _countView.setMinValue(Consts.VALUE_MIN_FLOOR_AREA);
                _countView.setValue(Consts.VALUE_MIN_FLOOR_AREA);
                _countView.setImage(R.drawable.floor);
                _countView.setContentHint(getString(R.string.hint_floor));
                _countView.setVisibility(View.VISIBLE);
                _countView.setListener(this);
                _durationSelected = Consts.FLOOR_SERVICE_DURATION*Consts.VALUE_MIN_FLOOR_AREA;
                break;
            case OrderType.SOFA:
                _titleView.setTitle(getString(R.string.cleaning_service_sofa));
                _priceInfoView.setText(R.string.price_sofa);
                _countView.setMinValue(Consts.VALUE_MIN_SOFA_COUNT);
                _countView.setValue(Consts.VALUE_MIN_SOFA_COUNT);
                _countView.setImage(R.drawable.sofa);
                _countView.setContentHint(getString(R.string.hint_sofa));
                _countView.setVisibility(View.VISIBLE);
                _countView.setListener(this);
                _durationSelected = Consts.SOFA_SERVICE_DURATION*Consts.VALUE_MIN_SOFA_COUNT;
                break;
            case OrderType.HOOD:
                _titleView.setTitle(getString(R.string.cleaning_service_range_hood));
                _priceInfoView.setText(R.string.price_hood);
                _durationSelected = Consts.HOOD_SERVICE_DURATION;
                break;
        }

        _staffSelectView.setOnClickListener(new StaffSelectView.onClickListener() {
            @Override
            public void onClick() {
                int startMin = timeStringToInt(_timeSelected);
                int endMin = startMin + _durationSelected;
                StaffRequestInfo info = new StaffRequestInfo();
                info.date = _dateSelected;
                info.startMin = startMin;
                info.endMin = endMin;
                info.businessId = _serviceType;
                info.latitude = _addressSelected.latitude;
                info.longitude = _addressSelected.longitude;
                List<StaffInfo> list = _staffSelectView.getStaffList();
                int[] ids = null;
                if (list != null && !list.isEmpty()) {
                    ids = new int[list.size()];
                    int index = 0;
                    for (StaffInfo staff : list) {
                        ids[index++] = staff.id;
                    }
                }
                ServiceStaffListActivity_.intent(CleaningServiceOrderSubmitActivity.this)._targetCount(_countSelected)._requestInfo(info)._selectedStaffIds(ids).startForResult(REQUEST_CODE_CHOOSE_STAFF);
            }
        });
        getAddressList();
    }

    @Background
    void getAddressList()
    {
        _activityHelper.showLoadingDialog(null);
        AddressListResponse resp = _restClient.getAddressList();
        _activityHelper.dismissLoadingDialog();
        afterGetAddress(resp);
    }

    @UiThread
    void afterGetAddress(AddressListResponse resp)
    {
        if (!BaseResponse.hasError(resp))
        {
            if (resp.addresslist != null) {
                for (AddressInfo address : resp.addresslist) {
                    if (address.isdefault == 1) {
                        _addressView.setContentText(address.addressMain+address.addressSub);
                        _addressSelected = address;
                        break;
                    }
                }
            }
        }
    }

    private void showTimeDialog() {
        ActionSheetDialog dialog = new ActionSheetDialog(this);
        View custom = LayoutInflater.from(this).inflate(R.layout.dialog_select_service_time, null);
        final NumericTextView durationView = (NumericTextView)custom.findViewById(R.id.numDuration);
        durationView.setListener(new NumericTextView.NumberChangedListener() {
            @Override
            public void onNumberChanged(int value) {
                _durationSelected = value*60;
                setTimeArray(_schedules);
            }
        });
        final NumericTextView countView = (NumericTextView)custom.findViewById(R.id.numServant);
        countView.setListener(new NumericTextView.NumberChangedListener() {
            @Override
            public void onNumberChanged(int value) {
                _countSelected = value;
                _staffSelectView.setTargetCount(_countSelected);
                setTimeArray(_schedules);
            }
        });
        _timePicker = (ServiceTimePicker)custom.findViewById(R.id.timePicker);
        final ServiceTimePicker timePicker = _timePicker;
        timePicker.setListener(new ServiceTimePicker.DateSelectionChangeListener() {
            @Override
            public void onDateSelectionChanged(String date) {
                _dateSelected = date;
                getSchedule();
            }
        });
        durationView.setMinValue(3);
        durationView.setValue(_durationSelected/60);
        countView.setMinValue(1);
        countView.setValue(_countSelected);

        if (_serviceType != OrderType.NORMAL && _serviceType != OrderType.DEEP) {
            custom.findViewById(R.id.duration_count).setVisibility(View.GONE);
        }

        dialog.setCustomView(custom);
        dialog.setClickListener(new ActionSheetDialog.DialogClickListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }

            @Override
            public void onDone(DialogInterface dialog) {
                int count = countView.getValue();
                int duration = durationView.getValue();
                String date = timePicker.getDate();
                String time = timePicker.getTime();
                if (time != null)  {
                    _countSelected = count;
                    _durationSelected = duration;
                    _dateSelected = date;
                    _timeSelected = time;
                    _lastDateIndexSelected = timePicker.getDateIndex();
                    _lastTimeIndexSelected = timePicker.getTimeIndex();

                    _timeView.setContentText(date + " " + time);
                    _staffSelectView.setTargetCount(_countSelected);
                    clearStaffSelected();
                    updateStaffSelectView();
                }

                dialog.dismiss();
            }
        });


        dialog.setClickListener(new ActionSheetDialog.DialogClickListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }

            @Override
            public void onDone(DialogInterface dialog) {
                String date = _timePicker.getDate();
                String time = _timePicker.getTime();
                if (time != null) {
                    _dateSelected = date;
                    _timeSelected = time;
                    _lastDateIndexSelected = _timePicker.getDateIndex();
                    _lastTimeIndexSelected = _timePicker.getTimeIndex();

                    _timeView.setContentText(date + " " + time);
                    clearStaffSelected();
                    updateStaffSelectView();
                }
                dialog.dismiss();
            }
        });

        _timePicker.setDateSelection(_lastDateIndexSelected);
        dialog.show();
    }

    private int timeStringToInt(String time) {
        String[] t = time.split(":");
        int h = Integer.parseInt(t[0]);
        int m = Integer.parseInt(t[1]);
        return h*60+m;
    }

    private void clearStaffSelected() {
        _staffSelectView.clear();
    }

    private void updateStaffSelectView() {
        if (_addressSelected != null && _timeSelected != null) {
            _staffSelectView.setVisibility(View.VISIBLE);
            _staffSelectView.updateViews();
        }
        else {
            _staffSelectView.clear();
            _staffSelectView.setVisibility(View.GONE);
        }
    }

    @Background
    void getSchedule() {
        _activityHelper.showLoadingDialog(null);
        ScheduleResponse response = _restClient.getSchedule(_dateSelected, _serviceType, _addressSelected.longitude, _addressSelected.latitude);
        afterGetSchedule(response);
        _activityHelper.dismissLoadingDialog();
    }

    @UiThread
    void afterGetSchedule(ScheduleResponse response) {
        if (BaseResponse.hasError(response)) {
            _activityHelper.showToast(BaseResponse.getErrorMessage(response));
        }
        else {
            _schedules = response.list;
            setTimeArray(response.list);
        }
    }

    void setTimeArray(List<Schedule> schedules) {
        String[] times = null;
        if (_serviceType == OrderType.HOOD) {
            times = getResources().getStringArray(R.array.schedule_time_hood);
        }
        else {
            times = getResources().getStringArray(R.array.schedule_time);
        }
        List<ServiceTimeItem> items = new ArrayList<>();
        for (String time : times) {
            ServiceTimeItem item = new ServiceTimeItem();
            item.time = time;
            item.available = false;
            int timeIntStart = timeStringToInt(time);
            int timeIntEnd = timeIntStart + _durationSelected;
            if (schedules != null && schedules.size() > 0) {
                for (Schedule schedule : schedules) {
                    if (schedule.startMin <= timeIntStart && schedule.endMin >= timeIntEnd) {
                        if (schedule.count >= _countSelected)
                            item.available = true;
                    }
                }
            }
            items.add(item);
        }
        _timePicker.setTimeArray(_lastTimeIndexSelected, items);
    }

    @Background
    void submitOrder() {
        CreateOrderResponse response = null;
        if (_serviceType == OrderType.NORMAL) {
            CreateNormalCleanOrderRequest request = new CreateNormalCleanOrderRequest();
            request.addressId = _addressSelected.id;
            request.phone = _phoneView.getContentText();
            request.auntCount = _countSelected;
            request.date = _dateSelected;
            request.startMin = timeStringToInt(_timeSelected);
            request.endMin = request.startMin + _durationSelected;
            request.comment = _commentView.getContentText();
            List<StaffInfo> staffs = _staffSelectView.getStaffList();
            if (!staffs.isEmpty()) {
                request.specifiedAuntList = new ArrayList<>();
                for (StaffInfo staff : staffs) {
                    request.specifiedAuntList.add(staff.id);
                }
            }
            response = _restClient.createNormalCleanOrder(request);
        }
        else if (_serviceType == OrderType.DEEP) {
            CreateNormalCleanOrderRequest request = new CreateNormalCleanOrderRequest();
            request.addressId = _addressSelected.id;
            request.phone = _phoneView.getContentText();
            request.auntCount = _countSelected;
            request.date = _dateSelected;
            request.startMin = timeStringToInt(_timeSelected);
            request.endMin = request.startMin + _durationSelected;
            request.comment = _commentView.getContentText();
            List<StaffInfo> staffs = _staffSelectView.getStaffList();
            if (!staffs.isEmpty()) {
                request.specifiedAuntList = new ArrayList<>();
                for (StaffInfo staff : staffs) {
                    request.specifiedAuntList.add(staff.id);
                }
            }
            response = _restClient.createDeepCleanOrder(request);
        }
        else if (_serviceType == OrderType.FLOOR) {
            CreateFloorCleanOrderRequest request = new CreateFloorCleanOrderRequest();
            request.addressId = _addressSelected.id;
            request.phone = _phoneView.getContentText();
            request.auntCount = _countSelected;
            request.date = _dateSelected;
            request.startMin = timeStringToInt(_timeSelected);
            request.endMin = request.startMin + _durationSelected;
            request.area = _durationSelected/Consts.FLOOR_SERVICE_DURATION;
            request.comment = _commentView.getContentText();
            List<StaffInfo> staffs = _staffSelectView.getStaffList();
            if (!staffs.isEmpty()) {
                request.specifiedAuntList = new ArrayList<>();
                for (StaffInfo staff : staffs) {
                    request.specifiedAuntList.add(staff.id);
                }
            }
            response = _restClient.createFloorCleanOrder(request);
        }
        else if (_serviceType == OrderType.SOFA) {
            CreateSofaCleanOrderRequest request = new CreateSofaCleanOrderRequest();
            request.addressId = _addressSelected.id;
            request.phone = _phoneView.getContentText();
            request.auntCount = _countSelected;
            request.date = _dateSelected;
            request.startMin = timeStringToInt(_timeSelected);
            request.endMin = request.startMin + _durationSelected;
            request.sofaCount = _durationSelected/Consts.SOFA_SERVICE_DURATION;
            request.comment = _commentView.getContentText();
            List<StaffInfo> staffs = _staffSelectView.getStaffList();
            if (!staffs.isEmpty()) {
                request.specifiedAuntList = new ArrayList<>();
                for (StaffInfo staff : staffs) {
                    request.specifiedAuntList.add(staff.id);
                }
            }
            response = _restClient.createSofaCleanOrder(request);
        }
        else if (_serviceType == OrderType.HOOD) {
            CreateHoodCleanOrderRequest request = new CreateHoodCleanOrderRequest();
            request.addressId = _addressSelected.id;
            request.phone = _phoneView.getContentText();
            request.auntCount = _countSelected;
            request.date = _dateSelected;
            request.startMin = timeStringToInt(_timeSelected);
            request.endMin = request.startMin + _durationSelected;
            request.hoodsCount = 1;
            request.comment = _commentView.getContentText();
            List<StaffInfo> staffs = _staffSelectView.getStaffList();
            if (!staffs.isEmpty()) {
                request.specifiedAuntList = new ArrayList<>();
                for (StaffInfo staff : staffs) {
                    request.specifiedAuntList.add(staff.id);
                }
            }
            response = _restClient.createHoodCleanOrder(request);
        }

        submitOrderResult(response);
    }

    @UiThread
    void submitOrderResult(CreateOrderResponse response) {
        if (BaseResponse.hasError(response)) {
            _activityHelper.showToast(BaseResponse.getErrorMessage(response));
        }
        else {
            _activityHelper.showToast(getString(R.string.order_submit_success));
            OrderDetailActivity_.intent(this).mOrderId(response.orderId).start();
            finish();
        }
    }

    @Override
    public void onNumberChanged(int value) {
        if (_serviceType == OrderType.FLOOR) {
            _durationSelected = Consts.FLOOR_SERVICE_DURATION * value;
        }
        else if (_serviceType == OrderType.HOOD) {
            _durationSelected = Consts.SOFA_SERVICE_DURATION * value;
        }
        clearStaffSelected();
        updateStaffSelectView();
    }
}
