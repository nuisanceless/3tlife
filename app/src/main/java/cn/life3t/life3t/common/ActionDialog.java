package cn.life3t.life3t.common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gghl.view.wheelcity.WheelView;

import java.util.ArrayList;
import java.util.List;

import cn.life3t.life3t.R;
import cn.life3t.life3t.utils.CommonUtils;

public class ActionDialog
{
	private Context mContext;
	private Dialog dialog;
    private List<SheetItem> sheetItemList;
    private int mSelectedItemValue = 0;

	private TextView txt_title;
	private TextView txt_cancel;
    private TextView txt_accomplish;
    private LinearLayout picker_Group;  // 里面的性别轮动选择模块
    private LinearLayout lLayout_content; // sheet layout
	private ScrollView sLayout_content;

	private boolean showTitle = false;
    private boolean showPickerView = false;


	public ActionDialog(Context context)
    {
		this.mContext = context;
	}

	public ActionDialog builder()
    {
        // 获取Dialog布局
		View view = LayoutInflater.from(mContext).inflate(R.layout.toast_view_actionsheet, null);

        // 设置Dialog最小宽度为屏幕宽度
		view.setMinimumWidth(CommonUtils.getScreenWidth(mContext));

        // 获取自定义Dialog布局中的控件
		sLayout_content = (ScrollView) view.findViewById(R.id.sLayout_content);
		lLayout_content = (LinearLayout) view.findViewById(R.id.lLayout_content);

        // gender,address,data 的轮动选择器
        picker_Group = (LinearLayout) view.findViewById(R.id.picker_Group);

        // title
		txt_title = (TextView) view.findViewById(R.id.txt_title);

        // 取消
		txt_cancel = (TextView) view.findViewById(R.id.txt_cancel);
		txt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

        // 完成
        txt_accomplish = (TextView)view.findViewById(R.id.txt_accomplish);


        // 定义Dialog布局和参数
        dialog = new Dialog(mContext, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.x = 0;
		lp.y = 0;
		dialogWindow.setAttributes(lp);

		return this;
	}

    public void dismissDialog ()
    {
        dialog.dismiss();
    }

    public int getSelectedItemValue ()
    {
        LinearLayout pickerLayout = (LinearLayout)picker_Group.getChildAt(0);
        WheelView wv = (WheelView)pickerLayout.findViewById(R.id.gender_picker);
        mSelectedItemValue = wv.getCurrentItem();
        return mSelectedItemValue;
    }

    /** 设置完成按钮监听器 */
    public ActionDialog setAccomplishClick (OnClickListener listener)
    {
        txt_accomplish.setOnClickListener(listener);
        return this;
    }

    /** 设置标题，默认为没有标题 */
	public ActionDialog setTitle(String title) {
        showTitle = true;
        if (title.equals(""))
        {
            showTitle = false;
        }
        else
        {
            txt_title.setText(title);
        }
		return this;
	}

    /** 设置轮动选择器 */
    public ActionDialog setPickerView(View view)
    {
        showPickerView = true;
        if (view == null)
        {
            showPickerView = false;
        }
        else
        {
            picker_Group.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        return this;
    }

	public ActionDialog setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
		return this;
	}

	public ActionDialog setCanceledOnTouchOutside(boolean cancel) {
		dialog.setCanceledOnTouchOutside(cancel);
		return this;
	}

    /**
     *
     * @param strItem
     *            条目名称
     * @param color
     *            条目字体颜色，设置null则默认蓝色
     * @param listener
     * @return
     */
	public ActionDialog addSheetItem(String strItem, SheetItemColor color, OnSheetItemClickListener listener) {
		if (sheetItemList == null) {
			sheetItemList = new ArrayList<SheetItem>();
		}
		sheetItemList.add(new SheetItem(strItem, color, listener));
		return this;
	}

    /** 设置条目布局 */
	private void setSheetItems() {
		if (sheetItemList == null || sheetItemList.size() <= 0) {
			return;
		}

		int size = sheetItemList.size();

        // TODO 高度控制，非最佳解决办法
        // 添加条目过多的时候控制高度
		if (size >= 7) {
			LayoutParams params = (LayoutParams) sLayout_content
					.getLayoutParams();
            params.height = CommonUtils.getScreenHeight(mContext) / 2;
			sLayout_content.setLayoutParams(params);
		}

        // 循环添加条目
		for (int i = 1; i <= size; i++) {
			final int index = i;
			SheetItem sheetItem = sheetItemList.get(i - 1);
			String strItem = sheetItem.name;
			SheetItemColor color = sheetItem.color;
			final OnSheetItemClickListener listener = (OnSheetItemClickListener) sheetItem.itemClickListener;

			TextView textView = new TextView(mContext);
			textView.setText(strItem);
			textView.setTextSize(18);
			textView.setGravity(Gravity.CENTER);

            // 背景图片，临时注释，因为没有好的ui资源
//			if (size == 1) {
//				if (showTitle) {
//					textView.setBackgroundResource(R.drawable.actionsheet_bottom_selector);
//				} else {
//					textView.setBackgroundResource(R.drawable.actionsheet_single_selector);
//				}
//			} else {
//				if (showTitle) {
//					if (i >= 1 && i < size) {
//						textView.setBackgroundResource(R.drawable.actionsheet_middle_selector);
//					} else {
//						textView.setBackgroundResource(R.drawable.actionsheet_bottom_selector);
//					}
//				} else {
//					if (i == 1) {
//						textView.setBackgroundResource(R.drawable.actionsheet_top_selector);
//					} else if (i < size) {
//						textView.setBackgroundResource(R.drawable.actionsheet_middle_selector);
//					} else {
//						textView.setBackgroundResource(R.drawable.actionsheet_bottom_selector);
//					}
//				}
//			}

            // 字体颜色
			if (color == null) {
				textView.setTextColor(Color.parseColor(SheetItemColor.Blue.getName()));
			} else {
				textView.setTextColor(Color.parseColor(color.getName()));
			}

            // 高度
			float scale = mContext.getResources().getDisplayMetrics().density;
			int height = (int) (45 * scale + 0.5f);
			textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height));

            // 点击事件
			textView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					listener.onClick(index);
					dialog.dismiss();
				}
			});

			lLayout_content.addView(textView);
		}
	}

	public void show() {
        if (showPickerView)
        {
            picker_Group.setVisibility(View.VISIBLE);
        }
        if (showTitle)
        {
            txt_title.setVisibility(View.VISIBLE);
        }
		setSheetItems();
		dialog.show();
	}

	public interface OnSheetItemClickListener {
		void onClick(int which);
	}

	public class SheetItem {
		String name;
		OnSheetItemClickListener itemClickListener;
		SheetItemColor color;

		public SheetItem(String name, SheetItemColor color, OnSheetItemClickListener itemClickListener)
        {
			this.name = name;
			this.color = color;
			this.itemClickListener = itemClickListener;
		}
	}

	public enum SheetItemColor {
		Blue("#037BFF"), Red("#FD4A2E");

		private String name;

		private SheetItemColor(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
