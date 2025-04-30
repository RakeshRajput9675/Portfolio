package com.example.interntask

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore.Images
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private val IMAGE_PICK_CODE = 1000
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private val itemList = mutableListOf<User>()
    private lateinit var dbHelper: DbHelper
    private lateinit var linearLayout: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DbHelper(this)
        recyclerView = findViewById(R.id.recyclerView)
        val addButton = findViewById<Button>(R.id.btn_add)
        linearLayout = findViewById(R.id.UserDetails)

        imageView = findViewById(R.id.profileImage)
//        Edit personal details

//        select the image from the gallery
imageView.setOnClickListener {
    openGallery()

}
        itemList.addAll(dbHelper.getAllUsers())

        adapter = ItemAdapter(itemList,
            onEditClick = { user, position -> showEditDialog(user, position) },
            onDeleteClick = { user, position -> deleteUser(user, position) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        addButton.setOnClickListener {
            val anim = AnimationUtils.loadAnimation(this, R.anim.scale_in)
            addButton.startAnimation(anim)
            showAddDialog()
        }
    }



    private fun openGallery() {
//        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val intent = Intent(Intent.ACTION_PICK, Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    // Handle the selected image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            // Get the image URI from the data
            val selectedImageUri = data?.data

            // Set the image URI to the ImageView
            imageView.setImageURI(selectedImageUri)
        }
    }
    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_item, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.et_title)
        val etDesc = dialogView.findViewById<EditText>(R.id.et_description)

       val dialog =  AlertDialog.Builder(this)
            .setTitle("Add Details")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val title = etTitle.text.toString().trim()
                val desc = etDesc.text.toString().trim()
                if (title.isNotEmpty() && desc.isNotEmpty()) {
                    val id = dbHelper.insertData(title, desc)
                    if (id != -1L) {
                        val user = User(id.toInt(), title, desc)
                        adapter.addItem(user)
                        Toast.makeText(this, "Added Successfully", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)

        val anim = AnimationUtils.loadAnimation(this, R.anim.scale_in)
        dialogView.startAnimation(anim)
        dialog.show()
    }

    private fun showEditDialog(user: User, position: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_item, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.et_title)
        val etDesc = dialogView.findViewById<EditText>(R.id.et_description)

        etTitle.setText(user.title)
        etDesc.setText(user.description)

        AlertDialog.Builder(this)
            .setTitle("Edit Details")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val updatedTitle = etTitle.text.toString().trim()
                val updatedDesc = etDesc.text.toString().trim()
                if (updatedTitle.isNotEmpty() && updatedDesc.isNotEmpty()) {
                    val success = dbHelper.updateUser(user.id, updatedTitle, updatedDesc)
                    if (success) {
                        val updatedUser = User(user.id, updatedTitle, updatedDesc)
                        adapter.updateItem(position, updatedUser)
                        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteUser(user: User, position: Int) {
        val success = dbHelper.deleteUser(user.id)
        if (success) {
            adapter.removeItem(position)
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
        }
    }
}

