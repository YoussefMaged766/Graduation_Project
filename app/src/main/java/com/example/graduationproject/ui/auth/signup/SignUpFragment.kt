package com.example.graduationproject.ui.auth.signup

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.graduationproject.R
import com.example.graduationproject.constants.Constants
import com.example.graduationproject.constants.Constants.Companion.dataStore
import com.example.graduationproject.constants.Constants.Companion.validateEmail
import com.example.graduationproject.constants.Constants.Companion.validateFirstname
import com.example.graduationproject.constants.Constants.Companion.validateLastname
import com.example.graduationproject.constants.Constants.Companion.validatePass
import com.example.graduationproject.databinding.FragmentSignUpBinding
import com.example.graduationproject.models.User
import com.example.graduationproject.utils.Status
import com.example.graduationproject.utils.getFilePathFromUri
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    lateinit var binding: FragmentSignUpBinding
    val viewModel: SignUpViewModel by viewModels()
    var imgUri: Uri? = null
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var imgBitmap: Bitmap? = null
    lateinit var loadFileGallery: ActivityResultLauncher<String>
    lateinit var loadFileCamera: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        validateBtn()
        onClicks()
        collectResponse()
        collectProgress()
        collectError()
        animation()
        checkPermission()

        Log.e( "onViewCreated: ",imgUri.toString() )

        loadFileGallery = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                imgUri = it
                Log.e("onViewCreated: ", imgUri.toString())
                Glide.with(requireContext()).load(it).into(binding.imgProfile)
            }

        }
        loadFileCamera =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    imgBitmap = data?.extras?.get("data") as Bitmap
                    imgUri = bitmapToUri(imgBitmap!!)

//                    Glide.with(requireContext()).load(imgUri).into(binding.imgProfile)
                    binding.imgProfile.setImageBitmap(imgBitmap)

                }
            }

        binding.btnCamera.setOnClickListener {
            handelPermission()
        }
    }

    private fun onClicks() {

        binding.apply {
            btnSignUp.setOnClickListener {

                if (userDataValidation(getUserData())) {
                    if (imgUri != null) {
                        viewModel.signUpUser(
                            imgUri,
                            getFilePathFromUri(requireContext(), imgUri!!, viewModel).toString(),
                            getUserData().firstName.toString(),
                            getUserData().lastName.toString(),
                            getUserData().email.toString(),
                            getUserData().password.toString(),
                            requireContext()
                        )
                    } else{
                        viewModel.signUpUser(
                           firstName =  getUserData().firstName.toString(),
                            lastName =  getUserData().lastName.toString(),
                            email =  getUserData().email.toString(),
                            password =  getUserData().password.toString(),
                            cnx =  requireContext()
                        )
                    }
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


    private fun collectResponse() {
        lifecycleScope.launch {
            viewModel.response.collect {

                Constants.customToast(
                    requireContext(),
                    requireActivity(),
                    it.message.toString()
                )

                Log.e("collectResponse: ", it.toString())
                viewModel.eventFlow.collect {
                    findNavController().navigate(it)
                }

            }
        }
    }


    private fun collectProgress() {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                viewModel.progress.collectLatest {
//                    binding.frameLoading.isVisible = it
                    if (it) Constants.showCustomAlertDialog(
                        requireActivity(),
                        R.layout.custom_alert_dailog,
                        false
                    )
                    else Constants.hideCustomAlertDialog()
                    Log.i(ContentValues.TAG, "collectProgress: $it")
                }
            }
        }


    }

    private fun collectError() {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                viewModel.error.collect {
                    Constants.customToast(requireContext(), requireActivity(), it)
                }
            }
        }

    }


    private fun getUserData(): User {
        val email = binding.txtEmail.text?.trim().toString()
        val password = binding.txtPassword.text.toString().trim()
        val firstName = binding.txtFirstName.text?.trim().toString()
        val lastName = binding.txtLastName.text.toString().trim()
        val confirmPassword = binding.txtPasswordConfirm.text?.trim().toString()

        return User(
            email = email,
            password = password,
            firstName = firstName,
            lastName = lastName,
            confirmPassword = confirmPassword
        )
    }

    private fun userRequestBody(): RequestBody {

        val gson = Gson()
        val userJsonString = gson.toJson(getUserData())
        val userRequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), userJsonString)
        Log.e("userRequestBody: ", userJsonString.toString())

        return userRequestBody
    }

    private fun saveDrawableToFile(
        context: Context,
        @DrawableRes drawableResId: Int,
        fileName: String
    ): File? {
        // Get the drawable from the resource ID
        val drawable = ContextCompat.getDrawable(context, drawableResId) ?: return null
        // Convert the drawable to a bitmap
        val bitmap = (drawable as BitmapDrawable).bitmap
        // Save the bitmap to a file
        val file = File(context.cacheDir, fileName)
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            Log.e("saveDrawableToFile: ", file.toString())
            return file
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun bitmapToFile(bitmap: Bitmap): File {

        // Get the directory for the app's private pictures directory.
        val file = File(requireContext().filesDir, "img_profile.jpg")

        try {
            // Convert bitmap to byte array
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, fos)
            // Write the bytes to a file
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            Log.e("bitmapToFile: ", e.message.toString())
        }


        return file
    }

    private fun userDataValidation(user: User): Boolean {
        if (user.email!!.validateEmail() && user.password!!.validatePass() && user.password == user.confirmPassword
            && user.firstName?.validateFirstname()!! && user.lastName?.validateLastname()!!
        ) {
            return true
        }
        if (!user.email.validateEmail()) binding.txtEmailContainer.error =
            "Please enter a valid E-mail"
        if (!user.password!!.validatePass()) binding.txtPasswordContainer.error =
            "password should be at least 6 letters or numbers"
        if (!user.confirmPassword?.validatePass()!!) binding.txtPasswordConfirmContainer.error =
            "confirm password is not equal to password "
        if (!user.firstName?.validateFirstname()!!) binding.txtFirstNameContainer.error =
            "Firstname should be at least 3 letters "

        if (!user.lastName?.validateLastname()!!) binding.txtLastNameContainer.error =
            "Lastname should be at least 4 letters "
        if (user.password != user.confirmPassword) binding.txtPasswordConfirmContainer.error =
            "confirm password is not equal to password "

        binding.txtEmail.doOnTextChanged { _, _, _, _ ->
            binding.txtEmailContainer.error = null
        }
        binding.txtPassword.doOnTextChanged { _, _, _, _ ->
            binding.txtPasswordContainer.error = null
        }
        binding.txtPasswordConfirm.doOnTextChanged { _, _, _, _ ->
            binding.txtPasswordConfirmContainer.error = null
        }
        binding.txtFirstName.doOnTextChanged { _, _, _, _ ->
            binding.txtFirstNameContainer.error = null
        }
        binding.txtLastName.doOnTextChanged { _, _, _, _ ->
            binding.txtLastNameContainer.error = null
        }
        return false
    }


    private fun validateBtn() {
        binding.txtPasswordConfirm.doOnTextChanged { s, _, _, _ ->
            if (s?.length != 0) {
                binding.txtPasswordContainer.error = null
                binding.btnSignUp.setBackgroundResource(R.drawable.checkbox_checked)
                binding.btnSignUp.isEnabled = true
                binding.btnSignUp.setTextColor(Color.WHITE)
            } else {
                binding.btnSignUp.setBackgroundResource(R.drawable.edittext_border)
                binding.btnSignUp.isEnabled = false
                binding.btnSignUp.setTextColor(resources.getColor(R.color.validateTextBButton))
            }

        }
    }

    private fun animation() {
        val an = AnimationUtils.loadAnimation(requireContext(), R.anim.ftb)
        val an2 = AnimationUtils.loadAnimation(requireContext(), R.anim.ftb_edit_text)
        binding.apply {
            imgLogo.startAnimation(an)
            txtLogin.startAnimation(an)
            txtFirstNameContainer.startAnimation(an2)
            txtEmailContainer.startAnimation(an2)
            txtLastNameContainer.startAnimation(an2)
            txtPasswordContainer.startAnimation(an2)
            txtPasswordConfirmContainer.startAnimation(an2)
        }
    }
}