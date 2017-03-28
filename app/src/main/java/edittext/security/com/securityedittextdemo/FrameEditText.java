package edittext.security.com.securityedittextdemo;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Layout;
import android.text.Selection;
import android.text.StaticLayout;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
/**
 * <pre>
 * author: yun.wang
 * Time: 2017/03/28
 * Description: ${DISC}
 * Version: ${VERSION}
 * </pre>
 */
public class FrameEditText extends EditText 
{
	private String TAG = "FrameEditText";
	private int totalLength = 5;//default is 5
	
	private Paint paintRect;
	private Paint paintText;
	
	//frame width, height, dividerWidth
	private int rectFrameWidth = 0;
	private int rectFrameHeight = 0;
	private int FramedividerWidth = 0;
	
	//frame color
	private int defaultFrameColor = 0;
	private int selectionFramColor = 0;
	
	private Context myContext;
	private FontMetrics fm;
	private int textWidth = 0;
	private int textSize = 15;
	
	private List<String> textStringList;
	private int currentSelctionIndex = 0;
	private List<Rect> frameRectList;
	
	//frame edit text layout params
	private int layoutWidth = 0;
	private int layoutHeight = 0;
	

	public FrameEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Log.d(TAG, "FrameEditText_1");
		myContext = context;
		
		//get the attribute of totallenght
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.frame_edit_text);
		this.totalLength = ta.getInteger(R.styleable.frame_edit_text_totalLength, 5);
		ta.recycle();
		
		initFrameEditText();
	}

	public FrameEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, "FrameEditText_2");
		myContext = context;
		
//		String textColor = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "textColor");
//		textColor = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height");
//		textColor = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "digits");
//		textColor = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "textSize");
		
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.frame_edit_text);
		this.totalLength = ta.getInteger(R.styleable.frame_edit_text_totalLength, 5);
		ta.recycle();
		
		initFrameEditText();
	}

	public FrameEditText(Context context) {
		super(context);
		Log.d(TAG, "FrameEditText_3");
		myContext = context;
	}
	
	public void initFrameEditText()
	{
		Log.d(TAG, "initFrameEditText");
		//set single line
		this.setSingleLine(true);
		//set max length filter
		this.setFilters(new InputFilter[] {new InputFilter.LengthFilter(totalLength)});
		
		
		frameRectList = new ArrayList<Rect>();
		textStringList = new ArrayList<String>();
		currentSelctionIndex = 0;
		
		//init the frame color
		defaultFrameColor = getResources().getColor(R.color.frame_default_color);
		selectionFramColor = getResources().getColor(R.color.frame_selection_color);
		
		//init the rect paint
		if(paintRect == null)
		{
			paintRect = new Paint();
			paintRect.setColor(defaultFrameColor);
			paintRect.setStrokeWidth(5.0f);
			paintRect.setStyle(Style.STROKE);
		}
		
		//init the text paint
		if(paintText == null)
		{
			paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
			paintText.setColor(this.getCurrentTextColor());
			paintText.setTextSize(this.getTextSize());
			fm = paintText.getFontMetrics();
			Log.d(TAG, "initFrameEditText, testsize= " + this.getTextSize());
		}
		
		this.addTextChangedListener(new TextWatcherListener());
		
	}
	
	public int getFrameEditTextTotalLenght()
	{
		return this.totalLength;
	}
	
	private class TextWatcherListener implements TextWatcher
	{
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			Log.d(TAG, "beforeTextChanged, s = " + s);
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			Log.d(TAG, "onTextChanged, s = " + s);
			Log.d(TAG, "onTextChanged, count = " + count);
			
			
			//set the selection index of the rect
			if(s == null || s.length() <= 0)
			{
				currentSelctionIndex = 0;
			}
			else
			{
				if(s != null)
				{
					currentSelctionIndex = s.length() - 1;
					Log.d(TAG, "onTextChanged, s.length() = " + s.length());
				}
				
			}
			
			Log.d(TAG, "onTextChanged, currentSelctionIndex = " + currentSelctionIndex);
			
			//divide the s to StringList
			if(textStringList != null)
			{
				textStringList.clear();
			}
			for(int index = 0; index < s.length(); index ++)
			{
				char tempChar = s.charAt(index);
				char[] tempCharArray = new char[1];
				tempCharArray[0] = tempChar;
				String tempString = new String(tempCharArray);
				textStringList.add(tempString);
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			Log.d(TAG, "afterTextChanged, s = " + s);
			//dismiss error pop when text changed
			//dismissErrorPopMessage();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
		layoutWidth = this.getWidth();
		layoutHeight = this.getHeight();
		
		Log.d(TAG, "onLayout, layoutWidth= " + layoutWidth);
		Log.d(TAG, "onLayout, layoutHeight= " + layoutHeight);
		
		Log.d(TAG, "onLayout, rectFrameWidth= " + rectFrameWidth);
		Log.d(TAG, "onLayout, rectFrameHeight= " + rectFrameHeight);
		
		if(layoutWidth > 0 && layoutHeight > 0)
		{
			//only init frames once
			if(rectFrameWidth <= 0 && rectFrameHeight <= 0)
			{
				if(frameRectList != null)
				{
					frameRectList.clear();
				}
				
				FramedividerWidth = 8;
				layoutWidth = layoutWidth - FramedividerWidth;
				
				//calc the frame width and height
				rectFrameWidth = (layoutWidth / totalLength) - FramedividerWidth;
				rectFrameHeight = (layoutHeight - FramedividerWidth * 2);
				
				for(int i = 0; i < totalLength; i ++)
				{
					int recLeft = FramedividerWidth * (i + 1) + rectFrameWidth * i;
					int recTop = FramedividerWidth;
					int recRight = recLeft + rectFrameWidth;
					int recBottom = recTop + rectFrameHeight;
					Rect newRec = new Rect(recLeft, recTop, recRight, recBottom);
					frameRectList.add(newRec);
				}
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		//Log.d(TAG, "onDraw");
		
		//the selection always at the end of the text
		Selection.setSelection(this.getText(), this.getText().length());
//		this.setSelection(this.getText().length());
		
		//set the background to the same with the activity
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//clear bg
		Drawable bgDrawable = this.getBackground();
		if(bgDrawable != null)
		{
			if(bgDrawable instanceof BitmapDrawable || bgDrawable instanceof ColorDrawable)
			{
				bgDrawable.draw(canvas);
			}
			else
			{
				canvas.drawColor(this.getResources().getColor(R.color.frame_bg_color));
			}
		}
		else
		{
			canvas.drawColor(this.getResources().getColor(R.color.frame_bg_color));
		}

		if(frameRectList != null)
		{
			//Log.d(TAG, "onDraw, frameRectList.size: " + frameRectList.size());
			for(int i = 0; i < frameRectList.size(); i ++)
			{
				//draw rectangle
				Rect newRec = frameRectList.get(i);
				if(i == currentSelctionIndex)
				{
					paintRect.setColor(selectionFramColor);
				}
				else
				{
					paintRect.setColor(defaultFrameColor);
				}
				canvas.drawRect(newRec, paintRect);
				
				//draw text
				String text = "";
				try
				{
					text = textStringList.get(i);
				}catch(Exception ex)
				{
					text = "";
				}
				
				textWidth = (int) paintText.measureText(text);
				int textTop = (int) (newRec.top + rectFrameHeight / 2 + Math.abs(fm.ascent) / 2);
				int textLeft = newRec.left + (newRec.width() - textWidth) / 2;
				canvas.drawText(text, textLeft, textTop, paintText);
			}
		}
	}
	
	
	
	@Override
	protected boolean setFrame(int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		Log.d(TAG, "setFrame");
		//updatePop();
		return super.setFrame(l, t, r, b);
	}



	private void chooseSize(PopupWindow pop, CharSequence text, TextView tv) {
        int wid = tv.getPaddingLeft() + tv.getPaddingRight();
        int ht = tv.getPaddingTop() + tv.getPaddingBottom();

//        int defaultWidthInPixels = this.getResources().getDimensionPixelSize(
//                com.android.internal.R.dimen.textview_error_popup_default_width);
        int defaultWidthInPixels = this.getWidth();
        Layout l = new StaticLayout(text, tv.getPaint(), defaultWidthInPixels,
                                    Layout.Alignment.ALIGN_NORMAL, 1, 0, true);
        float max = 0;
        for (int i = 0; i < l.getLineCount(); i++) {
            max = Math.max(max, l.getLineWidth(i));
        }

        /*
         * Now set the popup size to be big enough for the text plus the border capped
         * to DEFAULT_MAX_POPUP_WIDTH
         */
        int newPopWidth = wid + (int) Math.ceil(max);
        if(newPopWidth < this.getWidth())
        {
        	newPopWidth = this.getWidth() - this.getPaddingLeft() - this.getPaddingRight() - 
        			(int) (10 * this.getResources().getDisplayMetrics().density + 0.5f);
        }
        else
        {
        	newPopWidth = this.getWidth() - this.getPaddingLeft() - this.getPaddingRight() - 
        			(int) (10 * this.getResources().getDisplayMetrics().density + 0.5f);
        }
        pop.setWidth(newPopWidth);
        pop.setHeight(ht + l.getHeight());
    }
	

    /**
     * Returns the Y offset to make the pointy top of the error point
     * at the bottom of the error icon.
     */
    private int getErrorY() {
        /*
         * Compound, not extended, because the icon is not clipped
         * if the text height is smaller.
         */
        final int compoundPaddingTop = this.getCompoundPaddingTop();
        int vspace = this.getBottom() - this.getTop() -
        		this.getCompoundPaddingBottom() - compoundPaddingTop;

        int icontop = compoundPaddingTop +
                (vspace - this.getHeight()) / 2;

        /*
         * The "2" is the distance between the point and the top edge
         * of the background.
         */
        final float scale = this.getResources().getDisplayMetrics().density;
        return icontop + this.getHeight() - this.getHeight() -
                (int) (2 * scale + 0.5f);
    }
}
