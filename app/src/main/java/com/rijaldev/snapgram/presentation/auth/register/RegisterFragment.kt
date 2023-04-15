package com.rijaldev.snapgram.presentation.auth.register

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.widget.RxTextView
import com.rijaldev.snapgram.R
import com.rijaldev.snapgram.databinding.FragmentRegisterBinding
import com.rijaldev.snapgram.domain.common.Result
import com.rijaldev.snapgram.domain.model.auth.Register
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.registerResult.observe(viewLifecycleOwner, registerObserver)

        setUpView()
    }

    @SuppressLint("CheckResult")
    private fun setUpView() {
        binding?.apply {
            val passwordObservable = RxTextView.textChanges(edRegisterPassword)
            val confirmPasswordObservable = RxTextView.textChanges(edRegisterPasswordConfirm)
                .skipInitialValue()

            Observable.combineLatest(
                passwordObservable, confirmPasswordObservable
            ) { password, confirmPassword ->
                password.toString() == confirmPassword.toString()
            }.subscribe { isMatch ->
                binding?.edRegisterPasswordConfirm?.setError(if (!isMatch) getString(R.string.password_must_same) else null, null)
            }

            btnRegister.setOnClickListener {
                if (!isFormValid()) {
                    showSnackBar(getString(R.string.form_error))
                    return@setOnClickListener
                }
                btnRegister.showLoader()
                val name = edRegisterName.text.toString()
                val email = edRegisterEmail.text.toString()
                val password = edRegisterPassword.text.toString()
                viewModel.register(name, email, password)
            }

            val registerText = buildSpannedString {
                append(getString(R.string.sign_in_question)).bold {
                    append(getString(R.string.sign_in))
                }
            }
            tvSignIn.text = registerText
            tvSignIn.setOnClickListener {
                moveToLogin()
            }
        }
    }

    private val registerObserver = Observer<Result<Register>> { result ->
        when (result) {
            is Result.Loading -> showLoading(true)
            is Result.Success -> {
                showLoading(false)
                showSnackBar(result.data.message)

                val etEmail = binding?.edRegisterEmail?.text.toString().trim()
                val etPassword = binding?.edRegisterPasswordConfirm?.text.toString().trim()
                moveToLogin(etEmail, etPassword)
            }
            is Result.Error -> {
                showLoading(false)
                showSnackBar(result.message)
            }
        }
    }

    private fun isFormValid(): Boolean {
        binding?.apply {
            val name = edRegisterName.isValidName(edRegisterName.text)
            val email = edRegisterEmail.isValidEmail(edRegisterEmail.text)
            val password = edRegisterPassword.isValidPassword(edRegisterPassword.text)
            val confirmationPassword = edRegisterPasswordConfirm.isValidPassword(edRegisterPasswordConfirm.text)
            return name and email and password and confirmationPassword and edRegisterPasswordConfirm.error.isNullOrEmpty()
        }
        return false
    }

    private fun moveToLogin(email: String? = null, password: String? = null) {
        val toLogin = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment().also {
            it.email = email ?: ""
            it.password = password ?: ""
        }
        findNavController().navigate(toLogin)
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            if (isLoading) btnRegister.showLoader()
            else btnRegister.hideLoader()
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