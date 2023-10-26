package com.example.c001apk.ui.fragment.search.result

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.c001apk.R
import com.example.c001apk.logic.model.HomeFeedResponse
import com.example.c001apk.ui.activity.CopyActivity
import com.example.c001apk.ui.activity.app.AppActivity
import com.example.c001apk.ui.activity.feed.FeedActivity
import com.example.c001apk.ui.activity.topic.TopicActivity
import com.example.c001apk.ui.activity.user.UserActivity
import com.example.c001apk.ui.fragment.home.feed.FeedPicAdapter
import com.example.c001apk.util.CountUtil
import com.example.c001apk.util.EmojiUtil
import com.example.c001apk.util.ImageShowUtil
import com.example.c001apk.util.PubDateUtil
import com.example.c001apk.view.CenteredImageSpan
import com.example.c001apk.view.MyURLSpan
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.util.regex.Pattern

class SearchAdapter(
    private val mContext: Context,
    private val type: String,
    private val searchList: List<HomeFeedResponse.Data>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var loadState = 2
    val LOADING = 1
    val LOADING_COMPLETE = 2
    val LOADING_END = 3

    @SuppressLint("NotifyDataSetChanged")
    fun setLoadState(loadState: Int) {
        this.loadState = loadState
        notifyDataSetChanged()
    }

    class FeedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val uname: TextView = view.findViewById(R.id.uname)
        val message: TextView = view.findViewById(R.id.message)
        val pubDate: TextView = view.findViewById(R.id.pubDate)
        val like: TextView = view.findViewById(R.id.like)
        val reply: TextView = view.findViewById(R.id.reply)
        val avatar: ImageView = view.findViewById(R.id.avatar)
        val device: TextView = view.findViewById(R.id.device)
        var id = ""
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
    }

    class AppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val hotNum: TextView = view.findViewById(R.id.hotNum)
        val commentNum: TextView = view.findViewById(R.id.commentNum)
        val logo: ShapeableImageView = view.findViewById(R.id.logo)
        var entityType = ""
        var apkName = ""
    }

    class ProductTopicViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val hotNum: TextView = view.findViewById(R.id.hotNum)
        val commentNum: TextView = view.findViewById(R.id.commentNum)
        val logo: ShapeableImageView = view.findViewById(R.id.logo)
        var entityType = ""
        var aliasTitle = ""
    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val uname: TextView = view.findViewById(R.id.uname)
        val follow: TextView = view.findViewById(R.id.follow)
        val fans: TextView = view.findViewById(R.id.fans)
        val act: TextView = view.findViewById(R.id.act)
        val avatar: ImageView = view.findViewById(R.id.avatar)
        var uid = ""
    }

    class FootViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val footerLayout: FrameLayout = view.findViewById(R.id.footerLayout)
        val indicator: CircularProgressIndicator = view.findViewById(R.id.indicator)
        val noMore: TextView = view.findViewById(R.id.noMore)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            0 -> {
                val view =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_search_feed, parent, false)
                val viewHolder = FeedViewHolder(view)
                viewHolder.itemView.setOnClickListener {
                    val intent = Intent(parent.context, FeedActivity::class.java)
                    intent.putExtra("type", "feed")
                    intent.putExtra("id", viewHolder.id)
                    intent.putExtra("uname", viewHolder.uname.text)
                    parent.context.startActivity(intent)
                }
                viewHolder.message.setOnClickListener {
                    val intent = Intent(parent.context, FeedActivity::class.java)
                    intent.putExtra("type", "feed")
                    intent.putExtra("id", viewHolder.id)
                    intent.putExtra("uname", viewHolder.uname.text)
                    parent.context.startActivity(intent)
                }
                viewHolder.itemView.setOnLongClickListener {
                    val intent = Intent(parent.context, CopyActivity::class.java)
                    intent.putExtra("text", viewHolder.message.text.toString())
                    parent.context.startActivity(intent)
                    true
                }
                viewHolder.message.setOnLongClickListener {
                    val intent = Intent(parent.context, CopyActivity::class.java)
                    intent.putExtra("text", viewHolder.message.text.toString())
                    parent.context.startActivity(intent)
                    true
                }
                viewHolder.avatar.setOnClickListener {
                    val intent = Intent(parent.context, UserActivity::class.java)
                    intent.putExtra("id", viewHolder.uname.text)
                    parent.context.startActivity(intent)
                }
                viewHolder.uname.setOnClickListener {
                    val intent = Intent(parent.context, UserActivity::class.java)
                    intent.putExtra("id", viewHolder.uname.text)
                    parent.context.startActivity(intent)
                }
                viewHolder
            }

            1 -> {
                val view =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_search_topic, parent, false)
                val viewHolder = AppViewHolder(view)
                viewHolder.itemView.setOnClickListener {
                    val intent = Intent(parent.context, AppActivity::class.java)
                    intent.putExtra("id", viewHolder.apkName)
                    parent.context.startActivity(intent)
                }
                viewHolder
            }

            3 -> {
                val view =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_search_user, parent, false)
                val viewHolder = UserViewHolder(view)
                viewHolder.itemView.setOnClickListener {
                    val intent = Intent(parent.context, UserActivity::class.java)
                    intent.putExtra("id", viewHolder.uname.text)
                    parent.context.startActivity(intent)
                }
                viewHolder
            }

            2 -> {
                val view =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_search_topic, parent, false)
                val viewHolder = ProductTopicViewHolder(view)
                viewHolder.itemView.setOnClickListener {
                    val intent = Intent(parent.context, TopicActivity::class.java)
                    intent.putExtra(
                        "title",
                        if (viewHolder.entityType == "product")
                            viewHolder.aliasTitle
                        else viewHolder.title.text
                    )
                    parent.context.startActivity(intent)
                }
                viewHolder
            }

            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_rv_footer, parent, false)
                FootViewHolder(view)
            }

        }

    }

    override fun getItemCount() = searchList.size + 1

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {

            is FootViewHolder -> {
                when (loadState) {
                    LOADING -> {
                        holder.footerLayout.visibility = View.VISIBLE
                        holder.indicator.visibility = View.VISIBLE
                        holder.indicator.isIndeterminate = true
                        holder.noMore.visibility = View.GONE

                    }

                    LOADING_COMPLETE -> {
                        holder.footerLayout.visibility = View.GONE
                        holder.indicator.visibility = View.GONE
                        holder.indicator.isIndeterminate = false
                        holder.noMore.visibility = View.GONE
                    }

                    LOADING_END -> {
                        holder.footerLayout.visibility = View.VISIBLE
                        holder.indicator.visibility = View.GONE
                        holder.indicator.isIndeterminate = false
                        holder.noMore.visibility = View.VISIBLE
                    }

                    else -> {}
                }
            }

            is FeedViewHolder -> {
                val feed = searchList[position]
                holder.id = feed.id
                holder.uname.text = feed.username
                if (feed.deviceTitle != "") {
                    holder.device.text = feed.deviceTitle
                    val drawable: Drawable = mContext.getDrawable(R.drawable.ic_device)!!
                    drawable.setBounds(
                        0,
                        0,
                        holder.device.textSize.toInt(),
                        holder.device.textSize.toInt()
                    )
                    holder.device.setCompoundDrawables(drawable, null, null, null)
                    holder.device.visibility = View.VISIBLE
                } else {
                    holder.device.visibility = View.GONE
                }
                holder.pubDate.text = PubDateUtil.time(feed.dateline)
                val drawable1: Drawable = mContext.getDrawable(R.drawable.ic_date)!!
                drawable1.setBounds(
                    0,
                    0,
                    holder.pubDate.textSize.toInt(),
                    holder.pubDate.textSize.toInt()
                )
                holder.pubDate.setCompoundDrawables(drawable1, null, null, null)

                val mess = Html.fromHtml(
                    StringBuilder(feed.message).append(" ").toString().replace("\n", " <br />"),
                    Html.FROM_HTML_MODE_COMPACT
                )
                val builder = SpannableStringBuilder(mess)
                val pattern = Pattern.compile("\\[[^\\]]+\\]")
                val matcher = pattern.matcher(builder)
                val urls = builder.getSpans(
                    0, mess.length,
                    URLSpan::class.java
                )
                for (url in urls) {
                    val myURLSpan = MyURLSpan(mContext, feed.id, url.url, null)
                    val start = builder.getSpanStart(url)
                    val end = builder.getSpanEnd(url)
                    val flags = builder.getSpanFlags(url)
                    builder.setSpan(myURLSpan, start, end, flags)
                    builder.removeSpan(url)
                }
                holder.message.text = builder
                holder.message.movementMethod = LinkMovementMethod.getInstance()
                while (matcher.find()) {
                    val group = matcher.group()
                    if (EmojiUtil.getEmoji(group) != -1) {
                        val emoji: Drawable =
                            mContext.getDrawable(EmojiUtil.getEmoji(group))!!
                        emoji.setBounds(
                            0,
                            0,
                            (holder.message.textSize * 1.3).toInt(),
                            (holder.message.textSize * 1.3).toInt()
                        )
                        val imageSpan = CenteredImageSpan(emoji)
                        builder.setSpan(
                            imageSpan,
                            matcher.start(),
                            matcher.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        holder.message.text = builder
                    }
                }
                holder.like.text = feed.likenum
                val drawableLike: Drawable = mContext.getDrawable(R.drawable.ic_like)!!
                drawableLike.setBounds(
                    0,
                    0,
                    holder.like.textSize.toInt(),
                    holder.like.textSize.toInt()
                )
                holder.like.setCompoundDrawables(drawableLike, null, null, null)

                holder.reply.text = feed.replynum
                val drawableReply: Drawable = mContext.getDrawable(R.drawable.ic_message)!!
                drawableReply.setBounds(
                    0,
                    0,
                    holder.like.textSize.toInt(),
                    holder.like.textSize.toInt()
                )
                holder.reply.setCompoundDrawables(drawableReply, null, null, null)
                ImageShowUtil.showAvatar(holder.avatar, feed.userAvatar)

                if (feed.picArr?.isNotEmpty() == true) {
                    holder.recyclerView.visibility = View.VISIBLE
                    val mAdapter = FeedPicAdapter(feed.picArr)
                    val count =
                        if (feed.picArr.size < 3) feed.picArr.size
                        else 3
                    val mLayoutManager = GridLayoutManager(mContext, count)
                    val minorSpace = mContext.resources.getDimensionPixelSize(R.dimen.minor_space)
                    val normalSpace = mContext.resources.getDimensionPixelSize(R.dimen.normal_space)
                    holder.recyclerView.apply {
                        setPadding(normalSpace, 0, minorSpace, minorSpace)
                        adapter = mAdapter
                        layoutManager = mLayoutManager
                    }
                } else holder.recyclerView.visibility = View.GONE
            }

            is AppViewHolder -> {
                val app = searchList[position]
                holder.apkName = app.apkname
                holder.title.text = app.title
                holder.commentNum.text = app.commentCount + "讨论"
                holder.hotNum.text = app.downCount + "下载"
                ImageShowUtil.showIMG(holder.logo, app.logo)
            }

            is UserViewHolder -> {
                val user = searchList[position]
                holder.uid = user.uid
                holder.uname.text = user.username
                holder.follow.text = "${user.follow}关注"
                holder.fans.text = "${user.fans}粉丝"
                holder.act.text = PubDateUtil.time(user.logintime) + "活跃"
                ImageShowUtil.showAvatar(holder.avatar, user.userAvatar)
            }

            is ProductTopicViewHolder -> {
                val topic = searchList[position]
                holder.title.text = topic.title
                holder.hotNum.text = topic.hotNumTxt + "热度"
                holder.commentNum.text =
                    if (topic.entityType == "topic") topic.commentnumTxt + "讨论"
                    else topic.feedCommentNumTxt + "讨论"
                ImageShowUtil.showIMG(holder.logo, topic.logo)
                if (topic.entityType == "product")
                    holder.aliasTitle = topic.aliasTitle
                holder.entityType = topic.entityType
            }
        }


    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) -1
        else when (type) {
            "feed" -> 0
            "apk" -> 1
            "product" -> 2
            "user" -> 3
            else -> 2 // "topic"
        }
    }

}