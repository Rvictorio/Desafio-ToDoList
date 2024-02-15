package com.rafaelvictorio.todolist;

import com.rafaelvictorio.todolist.entity.Todo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TodolistApplicationTests {

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void testCreateTodoSuccess() {
		var todo = new Todo("todo 1", "desc todo 1", false, 1);

		webTestClient
				.post()
				.uri("/todos")
				.bodyValue(todo)
				.exchange()
				.expectStatus().isCreated()
				.expectBody()
				.jsonPath("$").isArray()
				.jsonPath("$.length()").isEqualTo(1)
				.jsonPath("$[0].name").isEqualTo(todo.getName())
				.jsonPath("$[0].descricao").isEqualTo(todo.getDescricao())
				.jsonPath("$[0].realizado").isEqualTo(todo.isRealizado())
				.jsonPath("$[0].prioridade").isEqualTo(todo.getPrioridade());
	}

	@Test
	public void testCreateTodoFailure() {
		webTestClient
				.post()
				.uri("/todos")
				.bodyValue(
						new Todo("", "", false, 0))
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Sql("/import.sql")
	@Test
	public void testUpdateTodoSuccess() {
		var todo = new Todo(Todo.getId(), Todo.getName() + " Up", Todo.getName() + " Up", !Todo.isRealizado(),
				Todo.getPrioridade() + 1);

		webTestClient
				.put()
				.uri("/todos/" + Todo.getId())
				.bodyValue(todo)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$").isArray()
				.jsonPath("$.length()").isEqualTo(5)
				.jsonPath("$[0].name").isEqualTo(todo.getName())
				.jsonPath("$[0].descricao").isEqualTo(todo.getDescricao())
				.jsonPath("$[0].realizado").isEqualTo(todo.isRealizado())
				.jsonPath("$[0].prioridade").isEqualTo(todo.getPrioridade());
	}

	@Test
	public void testUpdateTodoFailure() {
		var unexinstingId = 1L;

		webTestClient
				.put()
				.uri("/todos/" + unexinstingId)
				.bodyValue(
						new Todo(unexinstingId, "", "", false, 0))
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Sql("/import.sql")
	@Test
	public void testDeleteTodoSuccess() {
		webTestClient
				.delete()
				.uri("/todos/" + Todos.get(0).getId())
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$").isArray()
				.jsonPath("$.length()").isEqualTo(4)
				.jsonPath("$[0].nome").isEqualTo(Todos.get(1).getNome())
				.jsonPath("$[0].descricao").isEqualTo(Todos.get(1).getDescricao())
				.jsonPath("$[0].realizado").isEqualTo(Todos.get(1).isRealizado())
				.jsonPath("$[0].prioridade").isEqualTo(Todos.get(1).getPrioridade());
	}

	@Test
	public void testDeleteTodoFailure() {
		var unexinstingId = 1L;

		webTestClient
				.delete()
				.uri("/todos/" + unexinstingId)
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Sql("/import.sql")
	@Test
	public void testListTodos() throws Exception {
		webTestClient
				.get()
				.uri("/todos")
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$").isArray()
				.jsonPath("$.length()").isEqualTo(5)
				.jsonPath("$[0]").isEqualTo(Todos.get(0))
				.jsonPath("$[1]").isEqualTo(Todos.get(1))
				.jsonPath("$[2]").isEqualTo(Todos.get(2))
				.jsonPath("$[3]").isEqualTo(Todos.get(3))
				.jsonPath("$[4]").isEqualTo(Todos.get(4));
	}
}