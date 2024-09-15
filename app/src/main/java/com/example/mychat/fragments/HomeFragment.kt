package com.example.mychat.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mychat.R
import com.example.mychat.SignInActivity
import com.example.mychat.adapter.OnUserClickListener
import com.example.mychat.adapter.RecentChatAdapter
import com.example.mychat.adapter.UserAdapter
import com.example.mychat.adapter.onRecentChatClicked
import com.example.mychat.databinding.FragmentHomeBinding
import com.example.mychat.modal.RecentChats
import com.example.mychat.modal.Users
import com.example.mychat.mvvm.ChatAppViewModel
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView


@Suppress("DEPRECATION")
class HomeFragment : Fragment() , OnUserClickListener, onRecentChatClicked{



    lateinit var rvUsers : RecyclerView
    lateinit var useradapter : UserAdapter
    lateinit var userViewModel : ChatAppViewModel
    lateinit var homebinding: FragmentHomeBinding
    lateinit var fbauth : FirebaseAuth
    lateinit var toolbar : Toolbar
    lateinit var circleImageView: CircleImageView
    lateinit var recentchatadapter : RecentChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        homebinding= DataBindingUtil.inflate(inflater,R.layout.fragment_home, container, false)

        return homebinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        userViewModel=ViewModelProvider(this).get(ChatAppViewModel::class.java)

        fbauth = FirebaseAuth.getInstance()

        toolbar= view.findViewById(R.id.toolbarMain)
        circleImageView = toolbar.findViewById(R.id.tlImage)

        homebinding.lifecycleOwner = viewLifecycleOwner

        useradapter= UserAdapter()
        rvUsers = view.findViewById((R.id.rvUsers))

        val layoutManagerUsers= LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        rvUsers.layoutManager=layoutManagerUsers

        userViewModel.getUsers().observe(viewLifecycleOwner, Observer {

            useradapter.setUserList(it)

            rvUsers.adapter=useradapter

        })


        useradapter.setOnUserClickListener(this)


        homebinding.logOut.setOnClickListener{

            fbauth.signOut()

            startActivity(Intent(requireContext(),SignInActivity::class.java))



        }

        userViewModel.imageUrl.observe(viewLifecycleOwner, Observer {


            Glide.with(requireContext()).load(it).into(circleImageView)


        })


        recentchatadapter= RecentChatAdapter()

        userViewModel.getRecentChats().observe(viewLifecycleOwner, Observer {

            homebinding.rvRecentChats.layoutManager = LinearLayoutManager(activity)

            recentchatadapter.setOnRecentList(it)
            homebinding.rvRecentChats.adapter  = recentchatadapter
        })

        recentchatadapter.setOnRecentChatListener(this)


        circleImageView.setOnClickListener{

            view?.findNavController()?.navigate(R.id.action_homeFragment_to_settingFragment)
        }


        }

    override fun onUserSelected(position: Int, users: Users) {

        val action= HomeFragmentDirections.actionHomeFragmentToChatFragment(users)

        view?.findNavController()?.navigate(action)





    }

    override fun getOnRecentChatClicked(position: Int, recentchatlist: RecentChats) {

        val action = HomeFragmentDirections.actionHomeFragmentToChatFromHomeFragment(recentchatlist)

        view?.findNavController()?.navigate(action)

    }
}


