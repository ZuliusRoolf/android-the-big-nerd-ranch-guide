import android.app.AlertDialog
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.bignerdranch.android.criminalintent.R

class ImageViewDialogFragment : DialogFragment() {
    companion object {
        private const val ARG_IMAGE_URI = "image_uri"

        fun newInstance(imageUri: Uri): ImageViewDialogFragment {
            val args = Bundle().apply {
                putParcelable(ARG_IMAGE_URI, imageUri)
            }
            return ImageViewDialogFragment().apply {
                arguments = args
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val imageUri = arguments?.getParcelable<Uri>(ARG_IMAGE_URI)

        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_image_view, null)
        val imageView = view.findViewById<ImageView>(R.id.imageView)

        imageUri?.let {
            imageView.setImageURI(it)
        }

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setCancelable(true)
            .create()
    }
}