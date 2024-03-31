package com.example.fyp2

import android.R
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.fyp2.databinding.FragmentDetectHairstyleBinding
import com.example.fyp2.databinding.FragmentHomeBinding
import com.example.fyp2.ml.FaceShapeClassifier
import com.example.fyp2.ml.ModelNew
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.w3c.dom.Text
import java.io.IOException

class DetectHairstyleFragment : Fragment() {
    private val OUR_REQUEST_CODE = 123
    lateinit var selectBtn: Button
    lateinit var predBtn: Button
    lateinit var resView: TextView
    lateinit var recommendBtn: Button
    lateinit var takephotoBtn : Button
    lateinit var imageView: ImageView
    lateinit var resGenderView : TextView
    private lateinit var binding: FragmentDetectHairstyleBinding
    lateinit var bitmap: Bitmap
    lateinit var result : String
    var genderSelected : String = "Male"
    private val nav by lazy { findNavController() }

    private var maxIdx : Int = 0

    lateinit var userId : String

    private lateinit var imageProcessor: ImageProcessor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the bundle arguments here if needed
        val bundle: Bundle? = arguments
        if (bundle != null) {
            userId = bundle.getString("userId", "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDetectHairstyleBinding.inflate(inflater, container, false)

        selectBtn = binding.btnSltPred
        predBtn = binding.btnPredict
        imageView = binding.imageView3
        resView = binding.result
        recommendBtn = binding.recommendButton
        takephotoBtn = binding.takePhotoBtn


        var labels = requireContext().assets.open("labels.txt").bufferedReader().readLines()
        //var genderlabels = requireContext().assets.open("genderlabels.txt").bufferedReader().readLines()

        //image processor
        var imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(300, 300, ResizeOp.ResizeMethod.BILINEAR ))
            .build()

        //add spinner for choosing male or female
        val spinner = binding.btnChooseGender
        val genders = arrayOf("Male", "Female")
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, genders)
        spinner.adapter = arrayAdapter

        //This spinner is to get the value item selected
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                genderSelected = genders[position]
                Log.d("Changing",genderSelected)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        selectBtn.setOnClickListener {
            //open the gallery
            val intent: Intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }


        //set the recommnedation button view gone first
        binding.recommendButton.visibility = View.GONE

        predBtn.setOnClickListener {
            if (!::bitmap.isInitialized) {
                // Show a Toast message if no image is selected
                Toast.makeText(requireContext(), "Please select an image first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(bitmap)

            tensorImage = imageProcessor.process(tensorImage)

            val model = FaceShapeClassifier.newInstance(requireContext())

            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 300, 300, 3), DataType.FLOAT32)
            inputFeature0.loadBuffer(tensorImage.buffer)

            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

            maxIdx = 0

            outputFeature0.forEachIndexed { index, fl ->
                if (outputFeature0[maxIdx] < fl) {
                    maxIdx = index
                }
            }
            result = labels[maxIdx]
            resView.setText("Detected FaceShape:"+result)
            readRecommendation(genderSelected)
            model.close()
            binding.textViewRecommnedation.visibility = View.GONE
            binding.recommendButton.visibility = View.VISIBLE

        }

        recommendBtn.setOnClickListener {
            //navigate to hairstyle fragment
            val bundle1 = bundleOf("gender" to binding.btnChooseGender.selectedItem.toString().trim())
            val bundle2 = bundleOf("cat" to result)
            val bundle3 = bundleOf("id" to userId)

            val combinedBundle = Bundle().apply {
                putAll(bundle1)
                putAll(bundle2)
                putAll(bundle3)
            }


            nav.navigate(com.example.fyp2.R.id.recHairstyle, combinedBundle)

        }

        takephotoBtn.setOnClickListener {
            if(ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                takePicturePreview.launch(null)
            }
            else{
                requestPermission.launch(android.Manifest.permission.CAMERA)
            }
        }



        return binding.root
    }


    //request camera permission
    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){granted->
        if(granted){
            takePicturePreview.launch(null)
        }
        else{
            Toast.makeText(requireContext(), "Permission Denied!!", Toast.LENGTH_SHORT).show()
        }

    }

    private val takePicturePreview = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { image ->
        if (image != null) {
            bitmap = image
            imageView.setImageBitmap(bitmap)
            initializeImageProcessor()

        }
    }

    private fun initializeImageProcessor() {
        imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(28, 28, ResizeOp.ResizeMethod.BILINEAR))
            .build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 100 && data != null) {
            val uri = data.data
            try {
                // Use the uri to load the image
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                imageView.setImageBitmap(bitmap)
                initializeImageProcessor()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }




    private fun readRecommendation(gender: String) {
        Log.d("passing into readRe",gender)
        Log.d("faceshape passing",result)
        Firebase.firestore
            .collection("recommendation")
            .document(result) // Specify the faceshape here
            .get()
            .addOnSuccessListener { doc ->
                Log.d("Check doc",doc.toString())
                // Check if the document exists
                    val recommendation = doc.toObject(Recommendation::class.java)
                Log.d("Check recommendation",recommendation.toString())
                    if(recommendation!=null) {
                        Log.d("Got gender?",genderSelected)
                        //check whether its male or female
                        if (gender.equals("Male", ignoreCase = true)) {
                            binding.textViewRecommnedation.text = recommendation.Male
                        } else if (gender.equals("Female", ignoreCase = true)) {
                            binding.textViewRecommnedation.text = recommendation.Female

                        }
                    }

            }
    }




}