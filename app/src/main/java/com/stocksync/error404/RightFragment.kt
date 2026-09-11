package com.stocksync.error404

import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
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
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment

class RightFragment : Fragment(R.layout.fragment_right) {

    private lateinit var contentLayout: LinearLayout

    // Se guardan para poder liberarlos: con solo removeAllViews() el MediaPlayer
    // del VideoView sigue vivo (el audio continua) y el WebView se filtra.
    private var videoView: VideoView? = null
    private var webView: WebView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentLayout = view.findViewById(R.id.contentLayout)
        showOption("Perfil")
    }

    fun showOption(option: String) {
        if (!::contentLayout.isInitialized) return

        releaseMedia()
        contentLayout.removeAllViews()

        when {
            option == "Perfil" -> renderProfile()
            option == "Fotos" -> renderPhotos()
            option == "Video" -> renderVideo()
            option == "Web" -> renderWeb()
            option == "Botones" -> renderButtons()
            option.startsWith("Categoría: ") -> {
                val catName = option.substringAfter("Categoría: ")
                renderCategory(catName)
            }
            else -> renderProfile()
        }
    }

    private fun renderCategory(category: String) {
        val products = when (category) {
            "Electrónica" -> listOf(
                "Smartwatch" to "$450.000",
                "Audífonos BT" to "$120.000",
                "Cargador Rápido" to "$45.000"
            )
            "Hogar" -> listOf(
                "Lámpara LED" to "$85.000",
                "Cafetera" to "$190.000",
                "Juego de Sábanas" to "$150.000"
            )
            "Moda" -> listOf(
                "Chaqueta Denim" to "$120.000",
                "Tenis Urban" to "$210.000",
                "Gorra Sync" to "$35.000"
            )
            else -> listOf("Sin productos" to "-")
        }

        val title = TextView(requireContext()).apply {
            text = "Categoría: $category"
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(android.graphics.Color.BLACK)
            setPadding(16, 16, 16, 16)
        }

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        products.forEach { (name, price) ->
            val item = TextView(requireContext()).apply {
                text = "• $name - $price"
                textSize = 18f
                setTextColor(android.graphics.Color.DKGRAY)
                setPadding(8, 8, 8, 8)
            }
            container.addView(item)
        }

        contentLayout.addView(title)
        contentLayout.addView(container)
    }

    private fun releaseMedia() {
        videoView?.stopPlayback()
        videoView = null

        webView?.let { web ->
            web.stopLoading()
            web.loadUrl("about:blank")
            (web.parent as? ViewGroup)?.removeView(web)
            web.destroy()
        }
        webView = null
    }

    override fun onDestroyView() {
        releaseMedia()
        super.onDestroyView()
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
            setTextColor(android.graphics.Color.parseColor("#000000"))
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
                setBackgroundColor(android.graphics.Color.parseColor("#EEEEEE"))
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
                setTextColor(android.graphics.Color.parseColor("#000000"))
            }

            val descView = TextView(requireContext()).apply {
                text = description
                textSize = 14f
                setTextColor(android.graphics.Color.parseColor("#333333"))
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
            setTextColor(android.graphics.Color.parseColor("#000000"))
            setPadding(16, 16, 16, 8)
        }

        val video = VideoView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                400
            )
        }
        videoView = video

        val mediaController = MediaController(requireContext()).apply {
            setAnchorView(video)
        }
        video.setMediaController(mediaController)
        video.setVideoURI(Uri.parse("https://www.w3schools.com/html/mov_bbb.mp4"))

        // La preparación es asíncrona: solo se reproduce cuando el medio está listo.
        video.setOnPreparedListener { it.start() }
        video.setOnErrorListener { _, _, _ ->
            title.text = "No se pudo cargar el video. Revisa tu conexión a internet."
            true
        }

        contentLayout.addView(title)
        contentLayout.addView(video)
    }

    private fun renderWeb() {
        val urlInput = EditText(requireContext()).apply {
            hint = "https://www.google.com"
            setPadding(16, 16, 16, 16)
        }

        val loadButton = Button(requireContext()).apply {
            text = "Cargar página"
        }

        val web = WebView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            webViewClient = WebViewClient()
            webChromeClient = android.webkit.WebChromeClient()
            loadUrl("https://stock-sync-react.vercel.app/")
        }
        webView = web

        loadButton.setOnClickListener {
            val url = urlInput.text.toString().trim()
            if (url.isEmpty()) return@setOnClickListener
            val fullUrl = if (url.startsWith("http://") || url.startsWith("https://")) {
                url
            } else {
                "https://$url"
            }
            web.loadUrl(fullUrl)
        }

        contentLayout.addView(urlInput)
        contentLayout.addView(loadButton)
        contentLayout.addView(web)
    }

    private fun renderButtons() {
        val title = TextView(requireContext()).apply {
            text = "Acciones rápidas"
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(android.graphics.Color.parseColor("#000000"))
            setPadding(16, 16, 16, 8)
        }

        val statusText = TextView(requireContext()).apply {
            text = "Sin acciones por ahora"
            textSize = 16f
            setTextColor(android.graphics.Color.parseColor("#000000"))
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
            setTextColor(android.graphics.Color.WHITE)
            setOnClickListener { statusText.text = "Enlace compartido" }
        }

        val switchWidget = SwitchCompat(requireContext()).apply {
            text = "Notificaciones activadas"
            isChecked = true
            setTextColor(android.graphics.Color.parseColor("#000000"))
        }

        contentLayout.addView(title)
        contentLayout.addView(buyButton)
        contentLayout.addView(saveButton)
        contentLayout.addView(shareButton)
        contentLayout.addView(switchWidget)
        contentLayout.addView(statusText)
    }
}
