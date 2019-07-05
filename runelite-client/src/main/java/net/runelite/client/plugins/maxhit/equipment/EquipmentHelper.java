/*
 * Copyright (c) 2019, Bartvollebregt <https://github.com/Bartvollebregt>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.maxhit.equipment;

import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Item;

public class EquipmentHelper
{

	public static boolean wearsItemSet(Item[] equipedItems, EquipmentItemset itemSet)
	{
		return itemSet.getItems().stream().allMatch(item -> wearsItem(equipedItems, item));
	}

	private static boolean wearsItem(Item[] equipedItems, int itemId)
	{
		//System.out.println("2nd equipment slot item");
		for (int i = 0; i < equipedItems.length; i++){
			if (equipedItems[i].equals(itemId)){
				return true;
			}
		}
		return false;
	}

	public static boolean wearsItem(Item[] equipedItems, EquipmentSlotItem equipmentSlotItem)
	{
		//System.out.println("3rd wears item: ");
        /*for (Integer i : equipmentSlotItem.getItems()){
            System.out.println("Item ID: " + i);
        }*/
		return equipmentSlotItem.getItems().stream().anyMatch(itemId ->
				wearsItem(equipedItems,itemId)
		);
	}

}
