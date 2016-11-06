package cn.life3t.life3t.common;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import cn.life3t.life3t.R;
import cn.life3t.life3t.utils.CommonUtils;

/**
 * Created by Lei on 2015/5/27.
 */
public class ActionSheetDialog extends Dialog {
    Context _context;
    View _customView;
    View _cancelView;
    View _doneView;
    View _operationView;
    DialogClickListener _listener;

    public ActionSheetDialog(Context context) {
        super(context, R.style.ActionSheetDialogStyle);
        _context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_action_sheet);

        _cancelView = findViewById(R.id.cancel);
        _doneView = findViewById(R.id.done);
        _operationView = findViewById(R.id.operation_layout);
        final FrameLayout layout = (FrameLayout)findViewById(R.id.customPanel);
        layout.addView(_customView);

        _cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_listener != null) {
                    _listener.onCancel(ActionSheetDialog.this);
                }
            }
        });

        _doneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_listener != null) {
                    _listener.onDone(ActionSheetDialog.this);
                }
            }
        });

        //屏幕底部弹出Dialog，宽度为屏幕宽
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = CommonUtils.getScreenWidth(_context);
        dialogWindow.setAttributes(lp);
    }

    public void setCustomView(int resId) {
        LayoutInflater layoutInflater = LayoutInflater.from(_context);
        _customView = layoutInflater.inflate(resId, null);

        setCustomView(_customView);
    }

    public void setCustomView(View view) {
        _customView = view;
    }

    /** true 取消、完成这两个操作按钮可见 */
    public void setOperationViewVisible(boolean visible)
    {
        if (visible)
            _operationView.setVisibility(View.VISIBLE);
        else if (!visible)
        {
            _operationView.setVisibility(View.GONE);
        }
    }

    public void setClickListener(DialogClickListener listener) {
        _listener = listener;
    }

    public static interface DialogClickListener {
        public void onCancel(DialogInterface dialog);
        public void onDone(DialogInterface dialog);
    }
}
