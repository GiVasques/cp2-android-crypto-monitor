# 🪙 Android Crypto Monitor - CP2

## Informações Gerais

O **Android Crypto Monitor** é um aplicativo desenvolvido em Kotlin para fornecer a **cotação em tempo real do Bitcoin** utilizando dados de uma API.

O objetivo do projeto é **desenvolver a interface** combinando diversos componentes e demonstrar a capacidade de **consumir dados de APIs externas** para exibir informações sobre a cotação do Bitcoin.

## Arquivos .xml (layout)
- **activity_main.xml**

	Contém a estrutura principal da tela do aplicativo, com os componentes necessários para exibir as informações da cotação do Bitcoin e permitir a interação do usuário (botão de atualização).

- **component_toolbar_main.xml**

	Define a Toolbar.

- **component_quote_information.xml**

	Componente que exibe as informações da cotação do Bitcoin, incluindo o valor atual e a data da última atualização.

- **component_button_refresh.xml**

	Componente de botão de atualização, responsável por atualizar a cotação quando pressionado.

## Arquivos .kt

### Model
 - **TickerResponse.kt**: Contém as classes que representam a estrutura de dados da resposta da API do MercadoBitcoin.
 
 ### Service
 - **MercadoBitcoinService.kt**: Define a interface para consumir a API de cotação do Bitcoin, utilizando Retrofit.
 - **MercadoBitcoinServiceFactory**: Contém o método responsável por configurar e criar uma instância do Retrofit para realizar as chamadas à API.
 
 ### Principal
 - **MainActivity.kt**: Define o comportamento da tela principal do aplicativo, com a lógica para exibir a cotação do Bitcoin.

## Entendendo cada arquivo .kt
### 📌 TickerResponse.kt
Nesse arquivo criamos as classes que representam a **estrutura de dados** que a **API do Bitcoin** vai retornar.


A classe ***TickerResponse*** representa o objeto principal da resposta da API.

    class TickerResponse(  
    val ticker: Ticker  
    )
    
A classe ***Ticker*** representa o objeto que contém as informações detalhadas sobre a cotação do Bitcoin.

    class Ticker(  
        val high: String,  
        val low: String,  
        val vol: String,  
        val last: String,  
        val buy: String,  
        val sell: String,  
        val date: Long  
    )
A criação das duas classes foi necessária pois a API retorna um JSON com a chave `ticker` que contém os dados da cotação:

    {
      "ticker": {
        "high": "545000.00000000",
        "low": "535651.00000000",
        "vol": "17.92453792",
        "last": "540062.00000000",
        "buy": "539679.00000000",
        "sell": "540062.00000000",
        "open": "539598.00000000",
        "date": 1745789188,
        "pair": "BRLBTC"
      }
    }

---
### 📌 MercadoBitcoinService.kt
Nesse arquivo criamos uma **interface para consumir a API de um mercado de Bitcoin**. 

Nele temos uma única função, `getTicker()`, que faz uma chamada GET para o endpoint `api/BTC/ticker/` e retorna a resposta em um formato de `Response<TickerResponse>`.

    interface MercadoBitcoinService {  
        @GET("api/BTC/ticker/")  
        suspend fun getTicker(): Response<TickerResponse>  
    }
***obs:*** `suspend`é usada para marcar uma função como **suspensa** e permitir que ela seja chamada de forma assíncrona.

---
###  📌 MercadoBitcoinServiceFactory

Na classe `MercadoBitcoinServiceFactory` temos um método `create()` que configura o Retrofit definindo a URL base e adicionando o conversor JSON com Gson, e cria uma instância da interface `MercadoBitcoinService`.

    class MercadoBitcoinServiceFactory {  
        fun create(): MercadoBitcoinService {  
            val retrofit = Retrofit.Builder() // cria um builder para a instância do Retrofit 
                .baseUrl("https://www.mercadobitcoin.net/")  // define a URL base para as requisições. O endpoint que configuramos na interface MercadoBitcoinService (@GET("api/BTC/ticker/) será concatenado a essa URL base.
                .addConverterFactory(GsonConverterFactory.create())  // responsável por converter os dados da resposta da API em objetos Kotlin/Java.
                .build()  // cria a instância final do Retrofit após todas as configurações serem feitas no builder.
      
            return retrofit.create(MercadoBitcoinService::class.java)  // criação e retorno de uma instância de MercadoBitcoinService a partir do Retrofit configurado.
        }  
    }

---
### 📌 MainActivity.kt
Nesse arquivo definimos o comportamento da **tela principal do aplicativo**.

    // A classe MainActivity herda de AppCompatActivity e será a tela principal da aplicação
   
    class MainActivity : AppCompatActivity() {  
    
	    // O método onCreate é chamado quando a activity é criada.
	    // Ele recebe o parametro "savedInstanceState" que contém o estado salvo da atividade
        
        override fun onCreate(savedInstanceState: Bundle?) {  
            super.onCreate(savedInstanceState)  // Chama o método onCreate da classe pai (AppCompatActivity)
            setContentView(R.layout.activity_main)  // Cria a ligação entre o MainActivity e o nosso arquivo de layout principal "activity_main" criado na pasta "layout" dentro de "res"(R).
      
			// Abaixo foram declaradas variáveis que irão se referir aos componentes dos tipos Toolbar e Button criados dentro da pasta "layout". 
			// Foi necessário criar essas variáveis para conseguirmos configurar / modificar os elementos.
			
            val toolbarMain: Toolbar = findViewById(R.id.toolbar_main)  
            configureToolbar(toolbarMain)  // chamada do método que irá configurar a ToolBar
      
            val btnRefresh: Button = findViewById(R.id.btn_refresh)  
            btnRefresh.setOnClickListener {  // Definição de um "listener" para a ação de click no botão -> quando for clicado, executará a ação que estiver dentro das chaves.
      makeRestCall()  // Método que faz a chamada da API para obter os dados. Ele será executado no momento em que o "listener" capturar um click no botão. 
            }  
      }  
      
      // Criacao da função para configuracao da Toolbar
      
        private fun configureToolbar(toolbar: Toolbar) {  
            setSupportActionBar(toolbar)  // Configura a Toolbar para ser a barra de ação do aplicativo, substituindo a barra padrão do Android
            toolbar.setTitleTextColor(getColor(R.color.white))  // Define a cor do texto do título da toolbar como branca.
            supportActionBar?.setTitle(getText(R.string.app_title))  // Define o título da ActionBar para o texto que está na pasta "string" dentro de "res" (R).
            supportActionBar?.setBackgroundDrawable(getDrawable(R.color.primary)) // Define a cor de fundo da ActionBar para a cor "primary" declarada na pasta "color" dentro de "res" (R).
        }  
      
      // Criacao da função que será chamada quando o botão de atualização for clicado.
      
        private fun makeRestCall() {  
	        // A linha abaixo indica que o código será executado no Main thread e o couroutine será executado, rodando em segundo plano.            			 
	        CoroutineScope(Dispatchers.Main).launch {  
      try {  
				     
				     //Cria uma instância da classe MercadoBitcoinServiceFactory, e depois chama o método getTicker() contido dentro dela. Esse método retorna a cotação atual do Bitcoin, que é armazenado na variável "response".
				     
                    val service = MercadoBitcoinServiceFactory().create()  
                    val response = service.getTicker()  
      
                    if (response.isSuccessful) {  // Verifica se a resposta foi bem-sucedida (status HTTP 200). 
                    
	                    // Criação de variável que irá receber o corpo do JSON armazenado em response. 
	                    
                        val tickerResponse = response.body()  

						//Busca os componentes do tipo TextView que exibem o valor do bitcoin e a data da consulta na interface.
						
                        val lblValue: TextView = findViewById(R.id.lbl_value)  
                        val lblDate: TextView = findViewById(R.id.lbl_date)  
					      
					    //Criação de variáveis que armazenam a formatação correta, tanto do valor na variável "numberFormat" quanto da data na variável "sdf", e uso dessas variáveis para formatar os valores que vieram da API e alterar os componentes da interface para exibir esses valores.
					     
                        val lastValue = tickerResponse?.ticker?.last?.toDoubleOrNull()  
                        if (lastValue != null) {  
                            val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))  
                            lblValue.text = numberFormat.format(lastValue)  
                        }  
      
                        val date = tickerResponse?.ticker?.date?.let { Date(it * 1000L) }  
      val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())  
                        lblDate.text = sdf.format(date)  
      
                    } else {  // o que ocorrerá caso a resposta da API não tenha sido bem-sucedida.
                    
						// Criação de variável que verifica o código de resposta HTTP da API e gera uma mensagem de erro com base no código.
						
                        val errorMessage = when (response.code()) {  
                            400 -> "Bad Request"  
						    401 -> "Unauthorized"  
						    403 -> "Forbidden"  
						    404 -> "Not Found"  
						    else -> "Unknown error"  
					    }  

						// Exibe a mensagem de erro em um Toast (notificação na tela)
                        Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()  
                    }  
                } catch (e: Exception) {  // captura qualquer exceção ocorrida durante a chamada da API
	                // Exibe mensagem de erro caso ocorra qualquer falha na chamada da API
                    Toast.makeText(this@MainActivity, "Falha na chamada: ${e.message}", Toast.LENGTH_LONG).show()  
                }  
            }  
      }  
    }

## 📦Depêndencias utilizadas no projeto
- **AppCompat**: Essa biblioteca serve principalmente para duas coisas - *Compatibilidade* (permite recursos mais modernos em versões mais antigas do android) e *Componentes visuais melhorados* (a toolbar, por exemplo).

- **Retrofit**: permite que o Android se comunique com *APIs externas*.

- **Coroutines**: Os Coroutines facilitam o gerenciamento de *tarefas assíncronas* (que ocorrem em threads de fundo, sem travar o main thread). 
Utilizamos ele para que as chamadas da API (com o Retrofit) ocorram de forma assíncrona, o que significa que a interface do usuário pode continuar funcionando enquanto a requisição está sendo processada em segundo plano.

## Imagens para Evidência de Funcionamento do App
- Antes de clicar no botão "Atualizar"
  
![image](https://github.com/user-attachments/assets/d4f9ed15-ff4a-46fd-a42c-d1d056e7fc56)

- Ao clicar no botão "Atualizar"
  
![image](https://github.com/user-attachments/assets/46cc1ce3-af3e-4a9a-a72e-7c1280cdc03a)
