package com.stocksync.error404

import android.graphics.Color
import android.graphics.Paint
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
import android.widget.Toast
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
        showOption("Inicio")
    }

    fun showOption(option: String) {
        if (!::contentLayout.isInitialized) return

        releaseMedia()
        contentLayout.removeAllViews()

        when {
            option == "Inicio" -> renderHome()
            option == "Mi Cuenta" || option == "Perfil" -> renderProfile()
            option == "Catálogo" || option == "Fotos" -> renderPhotos()
            option == "Videos" || option == "Video" -> renderVideo()
            option == "Web" -> renderWeb()
            option == "Botones" -> renderButtons()
            option == "Categorías" -> renderCategoriesOverview()
            option.startsWith("Categoría: ") -> {
                val catName = option.substringAfter("Categoría: ")
                renderCategory(catName)
            }
            else -> renderHome()
        }
    }

    private fun renderHome() {
        val scrollView = ScrollView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#062E38"))
        }

        val mainContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        // 1. Search Bar
        val searchBox = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 12, 16, 12)
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(Color.parseColor("#0C2D3A"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 16
            }
        }
        val searchIcon = TextView(requireContext()).apply {
            text = "🔍 "
            textSize = 16f
        }
        val searchText = TextView(requireContext()).apply {
            text = "Buscar productos, marcas o categorías"
            textSize = 14f
            setTextColor(Color.parseColor("#8E9DA6"))
        }
        searchBox.addView(searchIcon)
        searchBox.addView(searchText)
        searchBox.setOnClickListener {
            Toast.makeText(requireContext(), "Búsqueda seleccionada", Toast.LENGTH_SHORT).show()
        }
        mainContainer.addView(searchBox)

        // 2. Offer Banner
        val bannerCard = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
            setBackgroundColor(Color.parseColor("#D84F52"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 24
            }
            setOnClickListener {
                Toast.makeText(requireContext(), "¡Oferta del Día seleccionada!", Toast.LENGTH_SHORT).show()
                showOption("Categoría: Electrónica")
            }
        }
        val badgeText = TextView(requireContext()).apply {
            text = "OFERTA DEL DÍA"
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#FFECEC"))
            setPadding(8, 4, 8, 4)
            setBackgroundColor(Color.parseColor("#B53B3E"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 8
            }
        }
        val bannerTitle = TextView(requireContext()).apply {
            text = "Hasta 50% dcto en Electrónica"
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 8
            }
        }
        val bannerDesc = TextView(requireContext()).apply {
            text = "Smartphones, notebooks, audífonos y accesorios con envío rápido."
            textSize = 13f
            setTextColor(Color.parseColor("#FFF0F0"))
        }
        bannerCard.addView(badgeText)
        bannerCard.addView(bannerTitle)
        bannerCard.addView(bannerDesc)
        mainContainer.addView(bannerCard)

        // 3. Categories Title & Row
        val catTitle = TextView(requireContext()).apply {
            text = "Categorías"
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }
        }
        mainContainer.addView(catTitle)

        val categoriesRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 24
            }
        }

        val cats = listOf("Hogar", "Moda", "Electrónica", "Deportes")
        val catIcons = listOf("🏠", "👕", "📱", "⚡")
        cats.forEachIndexed { index, cat ->
            val catCard = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(12, 16, 12, 16)
                setBackgroundColor(Color.parseColor("#0C2D3A"))
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    marginEnd = if (index < cats.size - 1) 8 else 0
                }
                setOnClickListener {
                    showOption("Categoría: $cat")
                }
            }
            val iconView = TextView(requireContext()).apply {
                text = catIcons[index]
                textSize = 22f
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 6
                }
            }
            val labelView = TextView(requireContext()).apply {
                text = cat
                textSize = 12f
                setTextColor(Color.parseColor("#D0D8E0"))
                gravity = Gravity.CENTER
            }
            catCard.addView(iconView)
            catCard.addView(labelView)
            categoriesRow.addView(catCard)
        }
        mainContainer.addView(categoriesRow)

        // 4. Featured Products Title & Grid
        val prodHeaderLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }
        }
        val prodTitle = TextView(requireContext()).apply {
            text = "Productos Destacados"
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }
        val verTodo = TextView(requireContext()).apply {
            text = "Ver todo"
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#D84F52"))
            setOnClickListener {
                showOption("Catálogo")
            }
        }
        prodHeaderLayout.addView(prodTitle)
        prodHeaderLayout.addView(verTodo)
        mainContainer.addView(prodHeaderLayout)

        val productsRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 24
            }
        }

        val products = listOf(
            Triple("Smartphone Ultra 128GB", "$249.990", "$299.990"),
            Triple("Audífonos Inalámbricos Pro", "$89.990", "$129.990")
        )

        products.forEachIndexed { index, (name, price, oldPrice) ->
            val pCard = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(12, 12, 12, 12)
                setBackgroundColor(Color.parseColor("#0C2D3A"))
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    marginEnd = if (index < products.size - 1) 12 else 0
                }
                setOnClickListener {
                    Toast.makeText(requireContext(), "Seleccionado: $name", Toast.LENGTH_SHORT).show()
                }
            }
            val pImgBox = LinearLayout(requireContext()).apply {
                gravity = Gravity.CENTER
                setBackgroundColor(Color.parseColor("#08222C"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    110
                ).apply {
                    bottomMargin = 10
                }
            }
            val pIcon = TextView(requireContext()).apply {
                text = if (index == 0) "📱" else "🎧"
                textSize = 32f
            }
            pImgBox.addView(pIcon)

            val pName = TextView(requireContext()).apply {
                text = name
                textSize = 14f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(Color.WHITE)
                maxLines = 2
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 6
                }
            }
            val priceRow = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }
            val pPrice = TextView(requireContext()).apply {
                text = price
                textSize = 15f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(Color.parseColor("#D84F52"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = 8
                }
            }
            val pOldPrice = TextView(requireContext()).apply {
                text = oldPrice
                textSize = 12f
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                setTextColor(Color.parseColor("#8E9DA6"))
            }
            priceRow.addView(pPrice)
            priceRow.addView(pOldPrice)

            pCard.addView(pImgBox)
            pCard.addView(pName)
            pCard.addView(priceRow)
            productsRow.addView(pCard)
        }
        mainContainer.addView(productsRow)

        // 5. Flash Sale Card
        val flashCard = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(16, 16, 16, 16)
            setBackgroundColor(Color.parseColor("#0C2D3A"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener {
                Toast.makeText(requireContext(), "¡Flash Sale seleccionada!", Toast.LENGTH_SHORT).show()
            }
        }
        val flashInfo = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }
        val flashHeader = TextView(requireContext()).apply {
            text = "Flash Sale"
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 4
            }
        }
        val flashDesc = TextView(requireContext()).apply {
            text = "Termina en menos de 2 horas.\nCompra ahora y aprovecha."
            textSize = 12f
            setTextColor(Color.parseColor("#A0B0BC"))
        }
        flashInfo.addView(flashHeader)
        flashInfo.addView(flashDesc)

        val timerBadge = TextView(requireContext()).apply {
            text = "01:48:32"
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            setPadding(12, 8, 12, 8)
            setBackgroundColor(Color.parseColor("#D84F52"))
        }

        flashCard.addView(flashInfo)
        flashCard.addView(timerBadge)
        mainContainer.addView(flashCard)

        scrollView.addView(mainContainer)
        contentLayout.addView(scrollView)
    }

    private fun renderCategoriesOverview() {
        val title = TextView(requireContext()).apply {
            text = "Categorías Principales"
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.BLACK)
            setPadding(16, 16, 16, 16)
        }
        val cats = listOf("Electrónica", "Hogar", "Moda")
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }
        cats.forEach { cat ->
            val btn = Button(requireContext()).apply {
                text = cat
                setBackgroundColor(Color.parseColor("#0B1F2A"))
                setTextColor(Color.WHITE)
                setOnClickListener {
                    showOption("Categoría: $cat")
                }
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 12
                }
            }
            container.addView(btn)
        }
        contentLayout.addView(title)
        contentLayout.addView(container)
    }

    private fun renderCategory(category: String) {
        val scrollView = ScrollView(requireContext())
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        val title = TextView(requireContext()).apply {
            text = "Categoría: $category"
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#000000"))
            setPadding(0, 0, 0, 16)
        }
        container.addView(title)

        val products = when (category) {
            "Electrónica" -> listOf(
                "Smartwatch Pro" to "Reloj inteligente para entrenamientos y notificaciones.",
                "Audífonos X9" to "Sonido premium con batería de larga duración.",
                "Laptop Aero" to "Portátil ligera para estudio y trabajo diario.",
                "Cámara Mini" to "Captura fotos y videos con calidad profesional.",
                "Gafas VR" to "Experiencia inmersiva para entretenimiento y gaming."
            )
            "Hogar" -> listOf(
                "Lámpara LED" to "Iluminación inteligente y de bajo consumo para tu hogar.",
                "Cafetera" to "Café fresco y recién hecho todas las mañanas.",
                "Juego de Sábanas" to "Suavidad y confort para un descanso óptimo."
            )
            "Moda" -> listOf(
                "Chaqueta Denim" to "Estilo clásico y duradero para cualquier ocasión.",
                "Tenis Urban" to "Comodidad y diseño moderno para el día a día.",
                "Gorra Sync" to "Protección y estilo urbano con ajuste perfecto."
            )
            else -> listOf("Sin productos" to "No hay descripción disponible.")
        }

        products.forEach { (titleText, descText) ->
            val card = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(12, 12, 12, 12)
                setBackgroundColor(Color.parseColor("#EEEEEE"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 12
                }
            }

            val titleView = TextView(requireContext()).apply {
                text = titleText
                typeface = Typeface.DEFAULT_BOLD
                textSize = 18f
                setTextColor(Color.parseColor("#000000"))
            }

            val descView = TextView(requireContext()).apply {
                text = descText
                textSize = 14f
                setTextColor(Color.parseColor("#333333"))
            }

            card.addView(titleView)
            card.addView(descView)
            container.addView(card)
        }

        scrollView.addView(container)
        contentLayout.addView(scrollView)
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

        val titleView = TextView(requireContext()).apply {
            text = "Catálogo General (Todos los productos)"
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#000000"))
            setPadding(0, 0, 0, 16)
        }
        container.addView(titleView)

        val allProducts = listOf(
            Triple("Electrónica", "Smartwatch Pro", "Reloj inteligente para entrenamientos y notificaciones."),
            Triple("Electrónica", "Audífonos X9", "Sonido premium con batería de larga duración."),
            Triple("Electrónica", "Laptop Aero", "Portátil ligera para estudio y trabajo diario."),
            Triple("Electrónica", "Cámara Mini", "Captura fotos y videos con calidad profesional."),
            Triple("Electrónica", "Gafas VR", "Experiencia inmersiva para entretenimiento y gaming."),
            Triple("Hogar", "Lámpara LED", "Iluminación inteligente y de bajo consumo para tu hogar."),
            Triple("Hogar", "Cafetera", "Café fresco y recién hecho todas las mañanas."),
            Triple("Hogar", "Juego de Sábanas", "Suavidad y confort para un descanso óptimo."),
            Triple("Moda", "Chaqueta Denim", "Estilo clásico y duradero para cualquier ocasión."),
            Triple("Moda", "Tenis Urban", "Comodidad y diseño moderno para el día a día."),
            Triple("Moda", "Gorra Sync", "Protección y estilo urbano con ajuste perfecto.")
        )

        allProducts.forEach { (category, title, description) ->
            val card = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(12, 12, 12, 12)
                setBackgroundColor(Color.parseColor("#EEEEEE"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 12
                }
            }

            val catBadge = TextView(requireContext()).apply {
                text = "[$category]"
                textSize = 12f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(Color.parseColor("#D84F52"))
                setPadding(0, 0, 0, 4)
            }

            val titleViewItem = TextView(requireContext()).apply {
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

            card.addView(catBadge)
            card.addView(titleViewItem)
            card.addView(descView)
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
            loadUrl("https://www.google.com/")
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
