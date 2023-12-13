package com.example.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ItemsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ItemsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var rvItems: RecyclerView
    private lateinit var etvName: EditText


    private val items = mutableListOf<Item>()
    private val db = Firebase.firestore
    //private lateinit var  currentDB: FirebaseFirestore
    private var auth = Firebase.auth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_items, container, false)

        etvName = view.findViewById(R.id.etvEnterItem)
        rvItems = view.findViewById(R.id.rvItems)
        rvItems.layoutManager = LinearLayoutManager(view.context)


        val adapter = ItemAdapter(view.context, items)
        rvItems.adapter = adapter

        val btnAdd: Button = view.findViewById(R.id.btnAdd)

        val imLogOut: ImageView = view.findViewById(R.id.imLogOut)

        imLogOut.setOnClickListener {
            auth.signOut()
            (activity as MainActivity)?.switchFragment(null, LoginFragment())

        }


        getData()

        btnAdd.setOnClickListener {
            addItem()
        }

        adapter.onClick = {

        }


        return view
    }

    private fun getData() {
        val user = auth.currentUser
        if (user == null) {
            return
        } else {
            db.collection("users").document(user.uid).collection("items").get().addOnSuccessListener { documentSnapshot ->
                items.clear()
                for (document in documentSnapshot.documents){
                    val item = document.toObject<Item>()
                    if( item != null) {
                        item.uid = document.id
                        items.add(item)

                    }
                }
                rvItems.adapter?.notifyDataSetChanged()
            }
        }

    }

    private fun addItem() {
        val user = auth.currentUser
        if (user == null) {
            return
        } else {
            val newItem = Item(etvName.text.toString(), false)
            db.collection("users").document(user.uid).collection("items").add(newItem).addOnSuccessListener {
                _ -> items.add(newItem)
                etvName.setText("")
                rvItems.adapter?.notifyItemInserted(items.indexOf(newItem))
            }
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ItemsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ItemsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}