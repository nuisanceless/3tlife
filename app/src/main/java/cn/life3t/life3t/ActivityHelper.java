package cn.life3t.life3t;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;

/**
 * Created by RexQian on 2014/10/12.
 */
@EBean
public class ActivityHelper {
    @RootContext
    Context mContext;

    AlertDialog mMessageBox;

    @UiThread
    public void showMessageBox(String title, String message)
    {
        if(mMessageBox == null)
        {
            mMessageBox = new AlertDialog.Builder(mContext)
                    .setPositiveButton(mContext.getString(R.string.confirm),
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create();
        }

        mMessageBox.setTitle(title);
        mMessageBox.setMessage(message);
        mMessageBox.show();
    }

    private Dialog mLoadDialog;

    public void showConfirmDialog(int titleRes, int messageRes, final DialogInterface.OnClickListener confirmListener)
    {
        showConfirmDialog(mContext.getString(titleRes), mContext.getString(messageRes), confirmListener);
    }

    @UiThread
    public void showConfirmDialog(String title, String message, final DialogInterface.OnClickListener confirmListener)
    {
        new AlertDialog.Builder(mContext).setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (confirmListener != null)
                        {
                            confirmListener.onClick(dialog, which);
                        }
                    }
                }).create().show();
    }

    @UiThread
    public void showLoadingDialog(String text)
    {
        dismissLoadingDialog();

        mLoadDialog = new Dialog(mContext, R.style.CustomProgressDialog);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_loading, null);
        mLoadDialog.setContentView(contentView);
        mLoadDialog.setCancelable(false);
        if (text != null)
        {
            TextView textView = (TextView)contentView.findViewById(R.id.emptyView);
            textView.setText(text);
        }
        mLoadDialog.show();
    }

    @UiThread(propagation= UiThread.Propagation.REUSE)
    public void dismissLoadingDialog()
    {
        if (mLoadDialog != null && mLoadDialog.isShowing())
            mLoadDialog.dismiss();

        mLoadDialog = null;
    }


    @UiThread
    public void showToast(String text)
    {
        if (text != null) {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View root = inflater.inflate(R.layout.toast, null);
            TextView view = (TextView) root.findViewById(R.id.toast_text);
            view.setText(text);
            Toast toast = new Toast(mContext);
            toast.setView(root);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    @UiThread
    public void postFinish(Activity activity, boolean isAffinity)
    {
        activity.finish();
//        if(isAffinity)
//        {
//            activity.finishAffinity();
//        }
//        else
//        {
//            activity.finish();
//        }
    }

    private AlertDialog mEditDialog;
    private EditText mDialogEditText;
    private OnEditDialogConfirmListener mEditDialogConfirmListener;
    public void showEditDialog(String title, String text, OnEditDialogConfirmListener l)
    {
        if(mEditDialog == null)
        {
            View view = View.inflate(mContext, R.layout.dialog_edit_view, null);
            mDialogEditText = (EditText) view.findViewById(R.id.edit_text);
            mEditDialog = new AlertDialog.Builder(mContext)
                    .setTitle(title)
                    .setView(view)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(mEditDialogConfirmListener != null)
                                mEditDialogConfirmListener.onConfirm(mDialogEditText.getText().toString());
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create();
        }
        setEditDialogListener(l);
        mDialogEditText.setText(text);
        mEditDialog.setTitle(title);
        mEditDialog.show();
    }

    public interface OnEditDialogConfirmListener
    {
        void onConfirm(String text);
    }

    private void setEditDialogListener(OnEditDialogConfirmListener l)
    {
        mEditDialogConfirmListener = l;
    }
}
