package sanjay.sherlock;

import android.app.Dialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;




public class _BottomSheetDialog1Fragment extends BottomSheetDialogFragment {
    @Override
    public void setupDialog(final Dialog dialog,int style)
    {
        super.setupDialog(dialog,style);
        View contentView=View.inflate(getContext(),R.layout.season3,null);
        dialog.setContentView(contentView);
    }
}