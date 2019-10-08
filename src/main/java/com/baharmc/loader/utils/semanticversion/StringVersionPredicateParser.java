/*
 * Copyright 2016 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baharmc.loader.utils.semanticversion;

import java.util.function.Predicate;

public final class StringVersionPredicateParser {
	public static Predicate<StringVersion> create(String text) throws VersionParsingException {
		final String compared = text.trim();

		if (compared.equals("*")) {
			return (t) -> true;
		} else {
			return (t) -> compared.equals(t.getFriendlyString());
		}
	}
}
