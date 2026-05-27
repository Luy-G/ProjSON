package projson.model

import projson.core.Reference

// exemplo de tarefa com dependencias
class Task(
    val description: String,
    val deadline: Date?,
    @Reference
    val dependencies: List<Task>
)
