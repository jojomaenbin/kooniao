package com.kooniao.travel.home;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.kooniao.travel.R;
import com.kooniao.travel.model.Comment;
import com.kooniao.travel.utils.DateUtil;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CommentAdapter extends BaseAdapter {

	public static enum Type {
		TYPE_REPLY_VISIBLE(0), // 显示回复按钮 
		TYPE_REPLY_INVISIBLE(1); // 不显示回复按钮

		public int type;

		Type(int type) {
			this.type = type;
		}
	}

	private Context context;
	private Type type;

	@SuppressLint("SimpleDateFormat")
	public CommentAdapter(Context context, Type type) {
		this.context = context;
		this.type = type;
	}

	private List<Comment> comments = new ArrayList<Comment>();

	/**
	 * 设置评论列表数据
	 * 
	 * @param comments
	 */
	public void setComments(List<Comment> comments) {
		if (comments != null) {
			this.comments = comments;
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return comments.size();
	}

	@Override
	public Object getItem(int position) {
		return comments.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_product_comment, null);
			viewHolder.avatarImageView = (ImageView) convertView.findViewById(R.id.iv_avatar);
			viewHolder.userNameTextView = (TextView) convertView.findViewById(R.id.tv_user_name);
			viewHolder.ratingBar = (RatingBar) convertView.findViewById(R.id.rb_product_comment);
			viewHolder.contenTextView = (TextView) convertView.findViewById(R.id.tv_product_comment_content);
			viewHolder.dateTextView = (TextView) convertView.findViewById(R.id.tv_product_comment_date);
			viewHolder.replyImageView = (ImageView) convertView.findViewById(R.id.iv_comment_reply);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final Comment comment = comments.get(position);
		// 加载头像
		ImageView avatarImageView = viewHolder.avatarImageView;
		String uri = comment.getCommentUserFace();
		ImageLoaderUtil.loadAvatar(ImageLoader.getInstance(), uri, avatarImageView);
		// 设置用户名
		viewHolder.userNameTextView.setText(comment.getCommentUname());
		// 评分
		viewHolder.ratingBar.setRating(comment.getCommentRank());
		// 评论内容
		viewHolder.contenTextView.setText(comment.getCommentContent());
		// 评论时间
		long dateStamp = comment.getCommentTime();
		String commentDate = DateUtil.timeDistanceString(dateStamp);
		viewHolder.dateTextView.setText(commentDate);
		// 是否显示回复评论按钮
		if (type.type == Type.TYPE_REPLY_VISIBLE.type) {
			viewHolder.replyImageView.setVisibility(View.VISIBLE);
		} else {
			viewHolder.replyImageView.setVisibility(View.INVISIBLE);
		}

		if (listener != null) {
			viewHolder.replyImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					listener.onReplyCommentClick(position);
				}
			});
		}

		return convertView;
	}

	static class ViewHolder {
		ImageView avatarImageView; // 头像
		TextView userNameTextView; // 用户名
		RatingBar ratingBar; // 产品评分条
		TextView contenTextView; // 评论内容
		TextView dateTextView; // 评论时间
		ImageView replyImageView; // 回复评论
	}

	public interface ItemRequestListener {
		void onReplyCommentClick(int position);
	}

	private ItemRequestListener listener;

	public void setOnItemRequestListener(ItemRequestListener listener) {
		this.listener = listener;
	}

}
