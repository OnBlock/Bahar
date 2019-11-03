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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public final class SemanticVersionPredicateParser {

	private static final Map<String, Function<SemanticVersionImpl, Predicate<SemanticVersionImpl>>> PREFIXES;

	public static Predicate<SemanticVersionImpl> create(@NotNull String text) throws VersionParsingException {
		final List<Predicate<SemanticVersionImpl>> predicateList = new ArrayList<>();

		for (String s : text.split(" ")) {
			s = s.trim();

			if (s.isEmpty() || s.equals("*")) {
				continue;
			}

			Function<SemanticVersionImpl, Predicate<SemanticVersionImpl>> factory = null;

			for (Map.Entry<String, Function<SemanticVersionImpl, Predicate<SemanticVersionImpl>>> prefix : PREFIXES.entrySet()) {
				final String key = prefix.getKey();
				if (s.startsWith(key)) {
					factory = prefix.getValue();
					s = s.substring(key.length());
					break;
				}
			}

			final SemanticVersionImpl version = new SemanticVersionImpl(s, true);

			if (factory == null) {
				factory = PREFIXES.get("=");
			} else if (version.hasWildcard()) {
				throw new VersionParsingException("Prefixed ranges are not allowed to use X-ranges!");
			}

			predicateList.add(factory.apply(version));
		}

		if (predicateList.isEmpty()) {
			return s -> true;
		}

		return s -> {
			for (Predicate<SemanticVersionImpl> p : predicateList) {
				if (!p.test(s)) {
					return false;
				}
			}

			return true;
		};
	}

	static {
		PREFIXES = new LinkedHashMap<>();
		PREFIXES.put(">=", target -> source -> source.compareTo(target) >= 0);
		PREFIXES.put("<=", target -> source -> source.compareTo(target) <= 0);
		PREFIXES.put(">", target -> source -> source.compareTo(target) > 0);
		PREFIXES.put("<", target -> source -> source.compareTo(target) < 0);
		PREFIXES.put("=", target -> source -> source.compareTo(target) == 0);
		PREFIXES.put("~", target -> source -> source.compareTo(target) >= 0
				&& source.getVersionComponent(0) == target.getVersionComponent(0)
				&& source.getVersionComponent(1) == target.getVersionComponent(1));
		PREFIXES.put("^", target -> source -> source.compareTo(target) >= 0
				&& source.getVersionComponent(0) == target.getVersionComponent(0));
	}
}
