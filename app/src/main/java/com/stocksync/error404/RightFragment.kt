package com.stocksync.error404

import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.ScrollView
import android.widget.TextView
import android.widget.VideoView
import androidx.fragment.app.Fragment

class RightFragment : Fragment(R.layout.fragment_right) {

    private lateinit var contentLayout: LinearLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentLayout = view.findViewById(R.id.contentLayout)
        showOption("Perfil")
    }

    fun showOption(option: String) {
        if (!::contentLayout.isInitialized) return

        contentLayout.removeAllViews()

        when (option) {
            "Perfil" -> renderProfile()
            "Fotos" -> renderPhotos()
            "Video" -> renderVideo()
            "Web" -> renderWeb()
            "Botones" -> renderButtons()
            else -> renderProfile()
        }
    }

    private fun renderProfile() {
        val profileText = TextView(requireContext()).apply {
            text = "Nombre: Camila López\n\n" +
                "Estudios: Ingeniería de Sistemas - Politécnico Grancolombiano\n\n" +
                "Experiencia: 5 años en analítica comercial y atención al cliente\n\n" +
                "Especialidad: e-commerce, marketing digital y atención personalizada\n\n" +
                "Perfil profesional: Apasionada por crear experiencias de compra intuitivas, " +
                "rápidas y seguras para clientes que buscan productos con valor y confianza. " +
                "Su enfoque combina tecnología, servicio y estrategia para ofrecer una mejor " +
                "atención digital.\n\n" +
                "Habilidades: atención al cliente, ventas, investigación de mercado, " +
                "diseño de contenido, analítica y gestión de campañas."
            textSize = 16f
            setTextColor(android.graphics.Color.parseColor("#E8F2F6"))
            setPadding(16, 16, 16, 16)
            movementMethod = ScrollingMovementMethod()
        }

        val scrollView = ScrollView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        container.addView(profileText)
        scrollView.addView(container)
        contentLayout.addView(scrollView)
    }

    private fun renderPhotos() {
        val scrollView = ScrollView(requireContext())
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            setBackgroundColor(android.graphics.Color.parseColor("#0D2E3A"))
        }

        val products = listOf(
            "Smartwatch Pro" to "Reloj inteligente para entrenamientos y notificaciones.",
            "Audífonos X9" to "Sonido premium con batería de larga duración.",
            "Laptop Aero" to "Portátil ligera para estudio y trabajo diario.",
            "Cámara Mini" to "Captura fotos y videos con calidad profesional.",
            "Gafas VR" to "Experiencia inmersiva para entretenimiento y gaming."
        )

        products.forEach { (title, description) ->
            val card = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(12, 12, 12, 12)
                gravity = Gravity.CENTER_VERTICAL
                setBackgroundColor(android.graphics.Color.parseColor("#113D4D"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 12
                }
            }

            val info = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            val titleView = TextView(requireContext()).apply {
                text = title
                typeface = Typeface.DEFAULT_BOLD
                textSize = 18f
                setTextColor(android.graphics.Color.parseColor("#F7F4F1"))
            }

            val descView = TextView(requireContext()).apply {
                text = description
                textSize = 14f
                setTextColor(android.graphics.Color.parseColor("#DFEAEF"))
            }

            info.addView(titleView)
            info.addView(descView)
            card.addView(info)
            container.addView(card)
        }

        scrollView.addView(container)
        contentLayout.addView(scrollView)
    }

    private fun renderVideo() {
        val title = TextView(requireContext()).apply {
            text = "Video promocional"
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            setPadding(16, 16, 16, 8)
        }

        val videoView = VideoView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                400
            )
        }

        val mediaController = MediaController(requireContext())
        videoView.setMediaController(mediaController)
        videoView.setVideoURI(
            Uri.parse("https://www.w3schools.com/html/mov_bbb.mp4")
        )
        videoView.start()

        contentLayout.addView(title)
        contentLayout.addView(videoView)
    }

    private fun renderWeb() {
        val urlInput = EditText(requireContext()).apply {
            hint = "https://www.google.com"
            setPadding(16, 16, 16, 16)
        }

        val loadButton = Button(requireContext()).apply {
            text = "Cargar página"
        }

        val webView = WebView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            settings.javaScriptEnabled = true
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            webViewClient = WebViewClient()
            loadUrl("https://www.google.com")
        }

        loadButton.setOnClickListener {
            val url = urlInput.text.toString().trim()
            val fullUrl = if (url.startsWith("http://") || url.startsWith("https://")) {
                url
            } else {
                "https://$url"
            }
            webView.loadUrl(fullUrl)
        }

        contentLayout.addView(urlInput)
        contentLayout.addView(loadButton)
        contentLayout.addView(webView)
    }

    private fun renderButtons() {
        val title = TextView(requireContext()).apply {
            text = "Acciones rápidas"
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(android.graphics.Color.parseColor("#F9F7F5"))
            setPadding(16, 16, 16, 8)
        }

        val statusText = TextView(requireContext()).apply {
            text = "Sin acciones por ahora"
            textSize = 16f
            setTextColor(android.graphics.Color.parseColor("#E5EEF4"))
            setPadding(16, 16, 16, 16)
        }

        val buyButton = Button(requireContext()).apply {
            text = "Comprar ahora"
            setBackgroundColor(android.graphics.Color.parseColor("#D84F52"))
            setTextColor(android.graphics.Color.WHITE)
            setOnClickListener { statusText.text = "Compra confirmada" }
        }

        val saveButton = Button(requireContext()).apply {
            text = "Guardar"
            setBackgroundColor(android.graphics.Color.parseColor("#2B8FA0"))
            setTextColor(android.graphics.Color.WHITE)
            setOnClickListener { statusText.text = "Producto guardado" }
        }

        val shareButton = Button(requireContext()).apply {
            text = "Compartir"
            setBackgroundColor(android.graphics.Color.parseColor("#F0B38A"))
            setTextColor(android.graphics.Color.parseColor("#1D2B36"))
            setOnClickListener { statusText.text = "Enlace compartido" }
        }

        val switchWidget = android.widget.Switch(requireContext()).apply {
            text = "Notificaciones activadas"
            isChecked = true
            setTextColor(android.graphics.Color.parseColor("#E9F3F5"))
        }

        contentLayout.addView(title)
        contentLayout.addView(buyButton)
        contentLayout.addView(saveButton)
        contentLayout.addView(shareButton)
        contentLayout.addView(switchWidget)
        contentLayout.addView(statusText)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_right, container, false)
    }
}
