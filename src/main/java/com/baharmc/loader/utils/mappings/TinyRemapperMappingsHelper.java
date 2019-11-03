package com.baharmc.loader.utils.mappings;

import net.fabricmc.mappings.*;
import net.fabricmc.tinyremapper.IMappingProvider;
import net.fabricmc.tinyremapper.MemberInstance;

public class TinyRemapperMappingsHelper {
	private TinyRemapperMappingsHelper() {
	}

	public static IMappingProvider create(Mappings mappings, String from, String to) {
		return (classMap, fieldMap, methodMap) -> {
			for (ClassEntry entry : mappings.getClassEntries()) {
				classMap.put(entry.get(from), entry.get(to));
			}

			for (FieldEntry entry : mappings.getFieldEntries()) {
				EntryTriple fromTriple = entry.get(from);
				fieldMap.put(fromTriple.getOwner() + "/" + MemberInstance.getFieldId(fromTriple.getName(), fromTriple.getDesc()), entry.get(to).getName());
			}

			for (MethodEntry entry : mappings.getMethodEntries()) {
				EntryTriple fromTriple = entry.get(from);
				methodMap.put(fromTriple.getOwner() + "/" + MemberInstance.getMethodId(fromTriple.getName(), fromTriple.getDesc()), entry.get(to).getName());
			}
		};
	}

}
