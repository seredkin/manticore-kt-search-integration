import com.jillesvangurp.ktsearch.*
import com.jillesvangurp.searchdsls.querydsl.bool
import com.jillesvangurp.searchdsls.querydsl.matchPhrasePrefix
import com.jillesvangurp.searchdsls.querydsl.term
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer

class KtSearchManticoreExample {
  
  
  companion object {
    lateinit var manticoreContainer: GenericContainer<*>
    private lateinit var client: SearchClient
    
    @BeforeAll
    @JvmStatic
    fun setup() = runBlocking {
      
      manticoreContainer = GenericContainer("manticoresearch/manticore:latest")
        .withExposedPorts(9308)
      manticoreContainer.start()
      manticoreContainer.start()
      
      val host = manticoreContainer.host
      val port = manticoreContainer.getMappedPort(9308)
      
      
      client = SearchClient(
        KtorRestClient(host = host, port = port)
        //TODO : Manticore gives error
        /*
          Unexpected JSON token at offset 113: Expected start of the object '{', but had '2' instead at path: $.hits.total
          JSON input: ....."failed": 0 },"hits":{"total":2,"total_relation":"eq","max_s.....
          kotlinx.serialization.json.internal.JsonDecodingException: Unexpected JSON token at offset 113: Expected start of the object '{', but had '2' instead at path: $.hits.total
          JSON input: ....."failed": 0 },"hits":{"total":2,"total_relation":"eq","max_s.....
        */
      )
      
    }
    
    @AfterAll
    @JvmStatic
    fun teardown() = runBlocking {
      client.close()
      manticoreContainer.stop()
    }
  }
  
  
  @Serializable
  data class StateEntity(
    val state: String,
    val capital: String
  )
  
  private val indexName = "es-kt-index"
  
  
  @BeforeEach
  fun dropIndex() {
    kotlin.runCatching {
      runBlocking {
        client.deleteIndex(indexName)
      }
    }.fold({
      println("Index deleted")
    }, {
      println("Index doesn't exist")
    })
    createIndex()
    println("Index created")
  }
  
  private fun createIndex() {
    runBlocking {
      client.createIndex(indexName) {
        this.settings { }
        this.mappings(dynamicEnabled = false) {
          text(StateEntity::state)
          text(StateEntity::capital)
        }
      }
      client.getIndex(indexName).size shouldBe 1 // 1 is Index MetaData, not entries
    }
  }
  
  @Test
  fun indexAndSearch() {
    runBlocking {
      client.bulk(
        refresh = Refresh.WaitFor,
        bulkSize = 2,
      ) {
        index(
          doc = StateEntity(
            state = "Bavaria",
            capital = "Munich",
          ),
          index = indexName
        )
        index(
          doc = StateEntity(
            state = "Brandenburg",
            capital = "Berlin",
          ),
          index = indexName,
        )
      }
    }
    
    runBlocking {
      val results = client.search(indexName) {
        query = bool {
          must(
            // note how we can use property references here
            term(StateEntity::capital, "berlin"),
            matchPhrasePrefix(StateEntity::state, "bra")
          )
        }
      }
      println(results)
      results.parseHits<StateEntity>().first().capital shouldBe "Berlin"
    }
  }
  
}
