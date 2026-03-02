package net.alhrairyalbraa.kalmani.ui.on_boarding

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import net.alhrairyalbraa.kalmani.R

class ViewPagerAdapter(val context: Context): PagerAdapter() {

    private val images = listOf(R.drawable.ic_slider1, R.drawable.ic_slider2, R.drawable.ic_slider3)
    private val descriptions = listOf(
        "كلمني، أبلكيشن عربي يمكنك استخدامه إذا كنت تريد ترجمة لغة الاشارة العربية إلي نصوص وكلمات عن طرق الكاميرا",
        "حول الكلمات والجمل التي تريدها إلي لغة إشارة بكل سهولة فقط أدخل الكلمات أو الجمل و ستتحول للغة اشارة عن طريقة 3D character",
        "تعلم لغة الإشارة بكل سهولة واختبر نفسك عن طريق سكشن تعلم يمكنك مشاهدة ومن ثم تختبر نفسك وتتعرف إذا كنت تنفذها صح أم خطأ"
    )

    override fun getCount(): Int = descriptions.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean = (view == `object` as LinearLayout)

    @SuppressLint("MissingInflatedId")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.slider_layout, container, false)

        val slideImage = view.findViewById<View>(R.id.img_heading) as ImageView
        val slideDescription = view.findViewById<View>(R.id.heading_description) as TextView

        slideImage.setImageResource(images[position])
        slideDescription.text = descriptions[position]

        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}