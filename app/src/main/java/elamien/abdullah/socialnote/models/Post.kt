package elamien.abdullah.socialnote.models

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.api.load
import coil.transform.CircleCropTransformation
import com.google.firebase.Timestamp
import java.util.*

/**
 * Created by AbdullahAtta on 26-Aug-19.
 */
class Post {

	var documentName : String? = null
	var post : String? = null
	var authorName : String? = null
	var categoryName : String? = null
	var authorUId : String? = null
	var authorImage : String? = null
	var likes : List<Like>? = null
	var comments : List<Comment>? = null
	private var dateCreated : Timestamp? = null
	fun getDateCreated() : Date {
		return dateCreated!!.toDate()
	}

	fun setDateCreated(date : Timestamp) {
		this.dateCreated = date
	}

	companion object {
		@BindingAdapter("imageUrl")
		@JvmStatic
		fun loadAuthorImage(view : ImageView, imageUrl : String) {
			view.load(imageUrl) {
				crossfade(true)
				transformations(CircleCropTransformation())
			}

		}
	}
}