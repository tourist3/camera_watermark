package com.mobileheros.camera.ui.camera

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.mobileheros.camera.MainActivity
import com.mobileheros.camera.R
import com.mobileheros.camera.databinding.FragmentCameraBinding
import com.mobileheros.camera.databinding.FragmentHomeBinding
import com.mobileheros.camera.ui.dialog.UnlockPremiumDialog
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.gesture.Gesture
import com.otaliastudios.cameraview.gesture.GestureAction
import com.zackratos.ultimatebarx.ultimatebarx.statusBarOnly

class CameraFragment : Fragment() {

    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private var _binding: FragmentCameraBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        statusBarOnly {
            color = Color.TRANSPARENT
            fitWindow = true
            light = true
        }
        setupCamera()


        // Registers a photo picker activity launcher in single-select mode.
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
//            binding.imageGuide.setImageURI(uri)
            if (uri != null) {

            }
        }

        if (!XXPermissions.isGranted(requireContext(), Permission.READ_MEDIA_IMAGES)) {
            XXPermissions.with(this).permission(Permission.READ_MEDIA_IMAGES).request(null)
        }
    }

    private fun setupCamera() {
        binding.camera.apply {
            setLifecycleOwner(viewLifecycleOwner)
            addCameraListener(object : CameraListener() {
                override fun onPictureTaken(result: PictureResult) {
                    super.onPictureTaken(result)

                }

                override fun onVideoTaken(result: VideoResult) {
                    super.onVideoTaken(result)

                }
            })

            mapGesture(Gesture.PINCH, GestureAction.ZOOM); // Pinch to zoom!
            mapGesture(Gesture.TAP, GestureAction.AUTO_FOCUS); // Tap to focus!
            mapGesture(Gesture.LONG_TAP, GestureAction.TAKE_PICTURE); // Long tap to shoot!
            mapGesture(Gesture.SCROLL_HORIZONTAL, GestureAction.EXPOSURE_CORRECTION); // Long tap to shoot!
            mapGesture(Gesture.SCROLL_VERTICAL, GestureAction.FILTER_CONTROL_1); // Long tap to shoot!
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun pickUpImage() {
        if (context == null) return
        if (XXPermissions.isGranted(requireContext(), Permission.READ_MEDIA_IMAGES)) {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            XXPermissions.with(this).permission(Permission.READ_MEDIA_IMAGES)
                .request(object : OnPermissionCallback {
                    override fun onGranted(p0: MutableList<String>, granted: Boolean) {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }

                    override fun onDenied(
                        permissions: MutableList<String>,
                        doNotAskAgain: Boolean
                    ) {
                        super.onDenied(permissions, doNotAskAgain)
                        if (doNotAskAgain) {
                            context?.let { XXPermissions.startPermissionActivity(it, permissions) }
                        }
                    }
                })
        }

    }

}