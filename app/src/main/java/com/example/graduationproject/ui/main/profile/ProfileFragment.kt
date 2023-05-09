package com.example.graduationproject.ui.main.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import com.example.graduationproject.R
import com.example.graduationproject.adapter.ViewPagerAdapter
import com.example.graduationproject.constants.Constants
import com.example.graduationproject.databinding.FragmentProfileBinding
import com.example.graduationproject.ui.main.favorite.FavoriteFragment
import com.example.graduationproject.ui.main.wishlist.WishlistFragment
import com.example.graduationproject.utils.FadeOutTransformation
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
    private val fadeOutTransformation= FadeOutTransformation()
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var imgUri: Uri? = null
    lateinit var loadFileGallery : ActivityResultLauncher<String>
    lateinit var loadFileCamera : ActivityResultLauncher<Intent>

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
        setUpViewPager()
        checkPermission()

         loadFileGallery = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                imgUri = it

                Glide.with(requireContext()).asBitmap().load(it).into(binding.imgProfile)
            }

        }
         loadFileCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageBitmap = data?.extras?.get("data") as Bitmap
                binding.imgProfile.setImageBitmap(imageBitmap)
            }
        }

        binding.btnCamera.setOnClickListener {
            handelPermission()
        }
    }

    private fun setUpViewPager() {
        val adapter =
            ViewPagerAdapter(
                supportFragmentManager = requireActivity().supportFragmentManager,
                lifecycle = lifecycle
            )
        adapter.addFragment(WishlistFragment(), "Wishlist")
        adapter.addFragment(FavoriteFragment(), "Favorite")
        binding.viewpager.adapter = adapter
        binding.viewpager.isSaveEnabled = false
        binding.viewpager.setPageTransformer(fadeOutTransformation)
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
            binding.viewpager.setCurrentItem(tab.position, true)
        }.attach()


    }


private fun checkPermission() {

    permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isCameraPermissionGranted = permissions[Manifest.permission.CAMERA] ?: false
        val isGalleryPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false

        if (isCameraPermissionGranted && isGalleryPermissionGranted) {
            // Both permissions are granted, do something
            setUpBottomSheet()
        } else {
            // One or both permissions are not granted, show a message or take some other action
            Toast.makeText(requireContext(), "Camera and gallery permissions are required", Toast.LENGTH_SHORT).show()
        }
    }
}

    private fun handelPermission(){

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

    private fun setUpBottomSheet(){

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


}