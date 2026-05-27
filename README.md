# ProJson - JSON Generation with References

Trabalho realizado por:

* Luís Cardoso - 98861
* Afonso Taveira

ProJson é uma biblioteca em Kotlin para gerar JSON a partir de objetos, com suporte para referências entre objetos, anotações e plugins.

## Principais funcionalidades

* Conversão de objetos Kotlin para JSON;
* Suporte para `Map` e `Collection`;
* Referências com `@Reference`;
* Personalização com anotações:
  * `@JsonIgnore`: ignora uma propriedade;
  * `@JsonProperty("name")`: altera o nome de uma propriedade;
  * `@JsonString(Plugin::class)`: serializa objetos de uma classe como strings atraves de um plugin;
* Manipulação em memória de objetos e arrays JSON;
* Percurso da estrutura JSON com `accept`;
* Geração de texto JSON com `toJsonString`/`toString`.

## Exemplo de resultado

```json
[
  {
    "$id": "9e2e6c64-3236-45b7-8b8a-11271c69e4df",
    "$type": "Task",
    "description": "T1",
    "deadline": {
      "$type": "Date",
      "day": 30,
      "month": 2,
      "year": 2026
    },
    "dependencies": []
  },
  {
    "$id": "11fb194e-b75c-4f73-9c10-65df91b81352",
    "$type": "Task",
    "description": "T2",
    "deadline": {
      "$type": "Date",
      "day": 31,
      "month": 4,
      "year": 2026
    },
    "dependencies": []
  },
  {
    "$id": "d388f116-826f-4751-bdad-fb8cc152b968",
    "$type": "Task",
    "description": "T3",
    "deadline": null,
    "dependencies": [
      { "$ref": "9e2e6c64-3236-45b7-8b8a-11271c69e4df" },
      { "$ref": "11fb194e-b75c-4f73-9c10-65df91b81352" }
    ]
  }
]
```

## Instalação

Para gerar o JAR:

```bash
./gradlew jar
```

No Windows:

```bash
.\gradlew.bat jar
```

O ficheiro gerado fica em:

```text
build/libs/Projeto-1.0-SNAPSHOT.jar
```

Para usar a biblioteca noutro projeto, copia o JAR para uma pasta `lib` e adiciona-o ao `build.gradle.kts`:

```kotlin
dependencies {
    implementation(files("lib/Projeto-1.0-SNAPSHOT.jar"))
    implementation(kotlin("reflect"))
}
```

Exemplo simples:

```kotlin
fun main() {
    val myTask = Task("FinishProject", null, emptyList())
    val engine = ProJson()
    val json = engine.toJson(myTask)
    println(json.toJsonString())
}
```

## Como usar

### 1. Converter um objeto em JSON

```kotlin
data class Date(val day: Int, val month: Int, val year: Int)

val d = Date(31, 4, 2026)
val json = ProJson().toJson(d) as JsonObject
json.setProperty("year", 2027)
println(json)
```

### 2. Listas e Maps

```kotlin
val lista = listOf("a", null, "b")
val arr = ProJson().toJson(lista) as JsonArray
arr.add("c")

val mapa = mapOf("nome" to "Afonso", "idade" to 20)
val obj = ProJson().toJson(mapa) as JsonObject
```

### 3. Referências com `@Reference`

```kotlin
class Task(
    val description: String,
    val deadline: Date?,
    @Reference
    val dependencies: List<Task>
)
```

As propriedades anotadas com `@Reference` são serializadas com objetos `$ref`.

### 4. Alterar nomes e ignorar propriedades

```kotlin
class Task(
    @JsonProperty("desc")
    val description: String,

    @JsonIgnore
    val deadline: Date?,

    @JsonProperty("deps")
    val dependencies: List<Task>
)
```

### 5. Plugins com `@JsonString`

```kotlin
class DateAsText : DateToText {
    override fun toJsonString(obj: Any): String {
        val d = obj as Date
        return "%02d/%02d/%04d".format(d.day, d.month, d.year)
    }
}

@JsonString(DateAsText::class)
data class Date(val day: Int, val month: Int, val year: Int)

val d1 = Date(30, 2, 2026)
val d2 = Date(31, 4, 2026)
val json = ProJson().toJson(listOf(d1, d2)) as JsonArray
println(json)
```

Output:

```json
["30/02/2026", "31/04/2026"]
```

### 6. Percorrer o JSON

```kotlin
val json: JsonValue = ProJson().toJson(meuObjeto)

json.accept { node ->
    println(node::class.simpleName)
}
```

## Testes

Os testes estão em `src/test/kotlin/Tests.kt` e podem ser executados com:

```bash
./gradlew test
```

No Windows:

```bash
.\gradlew.bat test
```

## Estrutura

| Componente | Função |
|---|---|
| `JsonValue` e subclasses | Modelo JSON em memória |
| `JsonObject` | Representa objetos JSON |
| `JsonArray` | Representa arrays JSON |
| `JsonReference` | Representa objetos `{ "$ref": "..." }` |
| `ProJson.toJson` | Converte objetos Kotlin em JSON |
| `@JsonIgnore`, `@JsonProperty`, `@Reference` | Personalizam propriedades |
| `@JsonString` e `DateToText` | Permitem serialização por plugin |
