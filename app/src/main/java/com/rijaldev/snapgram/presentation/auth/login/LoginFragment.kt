package com.rijaldev.snapgram.presentation.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.rijaldev.snapgram.R
import com.rijaldev.snapgram.databinding.FragmentLoginBinding
import com.rijaldev.snapgram.domain.common.Result
import com.rijaldev.snapgram.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding
    private val viewModel by viewModels<LoginViewModel>()
    private val navArgs by navArgs<LoginFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.loginResult.collect { result ->
                    when (result) {
                        is Result.Loading -> showLoading(true)
                        is Result.Success -> {
                            showLoading(false)
                            moveToMain()
                        }
                        is Result.Error -> {
                            showLoading(false)
                            showSnackBar(result.message)
                        }
                    }
                }
            }
        }

        setUpView()
    }

    private fun setUpView() {
        binding?.apply {
            btnSignIn.setOnClickListener {
                if (!isFormValid()) {
                    showSnackBar(getString(R.string.form_error))
                    return@setOnClickListener
                }

                val email = edLoginEmail.text.toString()
                val password = edLoginPassword.text.toString()
                viewModel.login(email, password)
            }

            val registerText = buildSpannedString {
                append(getString(R.string.register_question)).bold {
                    append(getString(R.string.register))
                }
            }

            if (navArgs.email.isNotBlank() and navArgs.password.isNotBlank()) {
                edLoginEmail.setText(navArgs.email)
                edLoginPassword.setText(navArgs.password)
            }
            tvRegister.text = registerText
            tvRegister.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_loginFragment_to_registerFragment)
            )
        }
    }

    private fun isFormValid(): Boolean {
        binding?.apply {
            val email = edLoginEmail.isValidEmail(edLoginEmail.text)
            val password = edLoginPassword.isValidPassword(edLoginPassword.text)
            return email and password
        }
        return false
    }

    private fun moveToMain() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            if (isLoading) btnSignIn.showLoader()
            else btnSignIn.hideLoader()
        }
    }

    private fun showSnackBar(message: String?) {
        Snackbar.make(
            requireActivity().window.decorView,
            message.toString(),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}