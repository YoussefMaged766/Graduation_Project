package com.example.graduationproject.ui.main.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.graduationproject.R
import com.example.graduationproject.adapter.ViewPagerAdapter
import com.example.graduationproject.constants.Constants
import com.example.graduationproject.constants.Constants.Companion.dataStore
import com.example.graduationproject.databinding.FragmentProfileBinding
import com.example.graduationproject.ui.auth.signup.SignUpViewModel
import com.example.graduationproject.ui.main.favorite.FavoriteFragment
import com.example.graduationproject.ui.main.wishlist.WishlistFragment
import com.example.graduationproject.utils.FadeOutTransformation
import com.example.graduationproject.utils.getFilePathFromUri
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var imgBitmap: Bitmap? = null
    var imgUri: Uri? = null
    lateinit var loadFileGallery: ActivityResultLauncher<String>
    lateinit var loadFileCamera: ActivityResultLauncher<Intent>
    private lateinit var dataStore: DataStore<Preferences>
    val viewModel by viewModels<ProfileViewModel>()
    val viewModel2 by viewModels<SignUpViewModel>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkPermission()
        editProfile()

        loadFileGallery = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                imgUri = it
                Glide.with(requireContext()).load(it).into(binding.imgProfile)
            }

        }
        loadFileCamera =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    imgBitmap = data?.extras?.get("data") as Bitmap
                    imgUri = bitmapToUri(imgBitmap!!)
                    binding.imgProfile.setImageBitmap(imgBitmap)
                }
            }

        binding.btnCamera.setOnClickListener {
            handelPermission()
        }

        getProfile()
    }




    private fun checkPermission() {

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val isCameraPermissionGranted = permissions[Manifest.permission.CAMERA] ?: false
            val isGalleryPermissionGranted =
                permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false

            if (isCameraPermissionGranted && isGalleryPermissionGranted) {
                // Both permissions are granted, do something
                setUpBottomSheet()
            } else {
                // One or both permissions are not granted, show a message or take some other action
                Toast.makeText(
                    requireContext(),
                    "Camera and gallery permissions are required",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun handelPermission() {

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Both permissions are already granted, open the camera or gallery
            setUpBottomSheet()
        } else {
            // One or both permissions are not granted, request them
            val permissions = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            permissionLauncher.launch(permissions)
        }
    }


    private fun updateProfile() {
        lifecycleScope.launch {
            val fullName = binding.txtName.text.toString().split(" ")
            val token = "Bearer ${getToken("userToken")}"
            val firstName = fullName[0]
            val lastName = fullName[1]
            val email = binding.txtEmail.text.toString()
            Log.e("updateProfile: ", lastName.toString())
            if (imgUri != null) {
                viewModel.updateProfile(
                    token = token,
                    fileUri = imgUri!!,
                    fileRealPath = getFilePathFromUri(
                        requireContext(),
                        imgUri!!,
                        viewModel2
                    ).toString(),
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    ctx = requireContext()
                )
            } else {
                viewModel.updateProfile(
                    token = token,
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    ctx = requireContext()
                )
            }


            viewModel.stateProfile.collect {
                if (it.success != null) {
                    Toast.makeText(requireContext(), it.success.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
                if (it.error != null) {
                    Toast.makeText(requireContext(), it.error.toString(), Toast.LENGTH_SHORT).show()
                }


                lifecycleScope.launch {

//                    if (it.isLoading) {
//                        Constants.showCustomAlertDialog(
//                            requireActivity(),
//                            R.layout.custom_alert_dailog,
//                            false
//                        )
//                    } else {
//                        Log.e( "updateProfile: ", it.isLoading.toString() )
//                        Constants.hideCustomAlertDialog()
//                    }
                    binding.progressBar.isIndeterminate = it.isLoading
                }
            }
        }
    }

    private fun bitmapToUri(bitmap: Bitmap): Uri {
        val imageFile = File(requireContext().cacheDir, "temp_image.png")
        val os = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
        os.flush()
        os.close()
        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            imageFile
        )
    }

    private fun getProfile() {
        lifecycleScope.launch {
            viewModel.getProfile("Bearer ${getToken("userToken")}")

            viewModel.getProfile.collect {
                if (it.userResponse?.results?.image != null) {
                    val imageString = it.userResponse.results.image
                    if (imageString.isNotBlank()) {
                        Glide.with(requireContext()).load("http://192.168.1.2:3000/$imageString")
                            .into(binding.imgProfile)
                    } else {
                        binding.imgProfile.setImageResource(R.drawable.photo1)
                    }

                }
                binding.txtName.setText(buildString {
                    append(it.userResponse?.results?.firstName)
                    append(" ")
                    append(it.userResponse?.results?.lastName)
                })

                binding.txtEmail.setText(it.userResponse?.results?.email)

            }
        }
    }

    private fun editProfile() {
        binding.btnEditProfile.setOnClickListener {
            if (binding.btnCamera.visibility == View.GONE) {
                binding.btnCamera.visibility = View.VISIBLE
                binding.btnEditProfile.text = "Save Changes"
                binding.txtName.isEnabled = true
                binding.txtEmail.isEnabled = true
                Log.e("editProfile: ","aloo" )

            } else {
                binding.btnCamera.visibility = View.GONE
                binding.btnEditProfile.text = "Edit Profile"
                binding.txtName.isEnabled = false
                binding.txtEmail.isEnabled = false
                Log.e("editProfile: ","aloo2" )
                updateProfile()
            }
        }
    }


    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        loadFileCamera.launch(intent)
    }

    private fun openGalley() {
//        getImage().launch("image/*")
        loadFileGallery.launch("image/*")
    }

    private fun setUpBottomSheet() {

        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet, null)
        val camera = view.findViewById<LinearLayout>(R.id.linearCamera)
        val gallery = view.findViewById<LinearLayout>(R.id.linearGallery)
        camera.setOnClickListener {
            openCamera()
            dialog.dismiss()
        }
        gallery.setOnClickListener {
            openGalley()
            dialog.dismiss()
        }
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    private suspend fun getToken(key: String): String? {
        dataStore = requireContext().dataStore
        val dataStoreKey: Preferences.Key<String> = stringPreferencesKey(key)
        val preference = dataStore.data.first()
        return preference[dataStoreKey]
    }
}