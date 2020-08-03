package com.DataRunner.CountryTown

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ppcomp.knu.`object`.MakerData
import com.ppcomp.knu.R

class MakerAdapter(
    val context: Context,               // MainActivity
    val makerList: ArrayList<MakerData>,     // 객체 list
    val itemClick: (MakerData) -> Unit)      // 객체 클릭시 실행되는 lambda 식
    : RecyclerView.Adapter<MakerAdapter.Holder>() {

    /**
     * 각 객체를 감싸는 Holder
     * bind 가 자동 호출되며 데이터가 매핑된다.
     * @author jungwoo
     */
    inner class Holder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val dateName = itemView.findViewById<TextView>(R.id.maker_name)
        val buttonMail = itemView.findViewById<ImageButton>(R.id.button_mail)
        val dataEmail = itemView.findViewById<TextView>(R.id.maker_email)
        val dataGit= itemView.findViewById<TextView>(R.id.maker_git)
        val dataAff= itemView.findViewById<TextView>(R.id.maker_aff)
        val dataImg= itemView.findViewById<ImageView>(R.id.maker_img)

        fun bind (data: MakerData, context: Context) {
            dateName.text = data.makerName
            dataEmail.text = data.makerEmail
            dataGit.text = data.makerGit
            dataAff.text = data.makerAff

            // Set loading image
            Glide.with(itemView)
                .load(R.drawable.loading_spinningwheel)
                .into(dataImg)

//            // Set image
//            val storage = Firebase.storage
//            var storageRef = storage.reference
//            storageRef.child(data.makerImg).downloadUrl.addOnSuccessListener {
//                // Got the download URL for 'users/me/profile.png'
//                Glide.with(itemView)
//                    .load(it)
//                    .into(dataImg)
//            }.addOnFailureListener {
//                // Handle any errors
//            }
            buttonMail.setOnClickListener { itemClick(data) }
        }
    }

    /**
     * 화면을 최초로 로딩하여 만들어진 View 가 없는 경우, xml 파일을 inflate 하여 ViewHolder 생성
     * @author jungwoo
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MakerAdapter.Holder {
        /* LayoutInflater는 item을 Adapter에서 사용할 View로 부풀려주는(inflate) 역할을 한다. */
        val view: View = LayoutInflater.from(context).inflate(R.layout.maker_item, parent, false)
        return Holder(view)
    }

    /**
     * onCreateViewHolder 에서 만든 view 와 실제 입력되는 각각의 데이터 연결
     * @author jungwoo
     */
    override fun onBindViewHolder(holder: MakerAdapter.Holder, position: Int) {
        holder.bind(makerList[position], context)
    }

    /**
     * RecyclerView 로 만들어지는 item 의 총 개수 반환
     * @author jungwoo
     */
    override fun getItemCount(): Int {
        return makerList.size
    }
}