package com.xX_deadbush_Xx.hocus.api.util;

import java.util.List;

import net.minecraft.util.NonNullList;

public class ListHelper {
	public static <T> NonNullList<T> toNonNullList(List<T> list) {
		NonNullList<T> nnlist = NonNullList.create();
		list.forEach(entry -> nnlist.add(entry));
		return nnlist;
	}
}
