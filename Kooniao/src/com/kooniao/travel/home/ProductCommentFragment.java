package com.kooniao.travel.home;

import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseFragment;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.LinearListLayout;
import com.kooniao.travel.home.CommentAdapter.Type;
import com.kooniao.travel.manager.ProductManager;
import com.kooniao.travel.manager.ProductManager.CommentListResultCallback;
import com.kooniao.travel.model.Comment;

/**
 * 产品评论
 * 
 * @author ke.wei.quan
 * 
 */
@EFragment(R.layout.fragment_product_comment)
public class ProductCommentFragment extends BaseFragment {
	@ViewById(R.id.progressBar)
	ProgressBar progressBar; // 进度
	@ViewById(R.id.ll_product_comment)
	LinearListLayout linearListLayout; // 产品线路
	@ViewById(R.id.layout_no_data)
	LinearLayout noDataLayout; // 无数据布局
	@ViewById(R.id.no_data_margin_top)
	View noDataMarginTopView;

	private int pid; // 产品id
	private int type; // 产品类型

	@Override
	public void onAttach(Activity activity) {
		pid = getArguments().getInt(Define.PID);
		type = getArguments().getInt(Define.TYPE);
		super.onAttach(activity);
	}

	private int pageNum = 1; // 页码

	@AfterViews
	void init() {
		initView();
		pageNum = 1;
		loadProductComments(pageNum);
	}

	private CommentAdapter adapter;

	/**
	 * 初始化界面
	 */
	private void initView() {
		adapter = new CommentAdapter(getActivity(), Type.TYPE_REPLY_INVISIBLE);
	}

	/**
	 * 请求评论列表
	 */
	private void loadProductComments(int pageNum) {
		ProductManager.getInstance().loadProductCommentList(type, pid, pageNum, new CommentListResultCallback() {

			@Override
			public void result(String errMsg, List<Comment> comments, int pageCount) {
				loadProductCommentsComplete(errMsg, comments);
			}
		});
	}

	/**
	 * 请求评论列表数据完成
	 * 
	 * @param errMsg
	 * @param comments
	 */
	@UiThread
	void loadProductCommentsComplete(String errMsg, List<Comment> comments) {
		progressBar.setVisibility(View.GONE);

		if (errMsg == null) {
			if (comments.isEmpty()) {
				noDataMarginTopView.setVisibility(View.VISIBLE); 
				noDataLayout.setVisibility(View.VISIBLE);
			} else {
				adapter.setComments(comments);
				linearListLayout.setBaseAdapter(adapter); 
			}
		} else {
			Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
		}
	}

}
