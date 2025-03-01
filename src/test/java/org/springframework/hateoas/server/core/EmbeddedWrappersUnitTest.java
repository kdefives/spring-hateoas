/*
 * Copyright 2015-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.hateoas.server.core;

import static org.assertj.core.api.Assertions.*;

import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.hateoas.LinkRelation;

/**
 * Unit tests for {@link EmbeddedWrappers}.
 *
 * @author Oliver Gierke
 */
class EmbeddedWrappersUnitTest {

	EmbeddedWrappers wrappers = new EmbeddedWrappers(false);

	/**
	 * @see #286
	 */
	@Test
	void createsWrapperForEmptyCollection() {

		EmbeddedWrapper wrapper = wrappers.emptyCollectionOf(String.class);

		assertEmptyCollectionValue(wrapper);
		assertThat(wrapper.getRel()).isEmpty();
		assertThat(wrapper.getRelTargetType()).isEqualTo(String.class);
	}

	/**
	 * @see #286
	 */
	@Test
	void createsWrapperForEmptyCollectionAndExplicitRel() {

		EmbeddedWrapper wrapper = wrappers.wrap(Collections.emptySet(), LinkRelation.of("rel"));

		assertEmptyCollectionValue(wrapper);
		assertThat(wrapper.getRel()).hasValue(LinkRelation.of("rel"));
		assertThat(wrapper.getRelTargetType()).isNull();
	}

	/**
	 * @see #286
	 */
	@Test
	void rejectsEmptyCollectionWithoutExplicitRel() {

		assertThatIllegalArgumentException().isThrownBy(() -> {
			wrappers.wrap(Collections.emptySet());
		});
	}

	@Test // #1335
	@SuppressWarnings("unchecked")
	void addsSupplierOfStreamByResolvingIt() {

		EmbeddedWrapper wrap = wrappers.wrap(new Streamable<>(Stream.of(1, 2, 3)));

		assertThat(wrap.getValue()).isInstanceOfSatisfying(Collection.class, it -> {
			assertThat(it).containsExactly(1, 2, 3);
		});
	}

	@SuppressWarnings("unchecked")
	private static void assertEmptyCollectionValue(EmbeddedWrapper wrapper) {

		assertThat(wrapper.getValue()) //
				.isInstanceOfSatisfying(Collection.class, it -> assertThat(it).isEmpty());
	}

	@RequiredArgsConstructor
	static class Streamable<T> implements Supplier<Stream<T>> {

		private final Stream<T> stream;

		/*
		 * (non-Javadoc)
		 * @see java.util.function.Supplier#get()
		 */
		@Override
		public Stream<T> get() {
			return stream;
		}
	}
}
