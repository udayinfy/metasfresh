package de.metas.handlingunits.impl;

/*
 * #%L
 * de.metas.handlingunits.base
 * %%
 * Copyright (C) 2015 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */


import java.util.List;

import org.adempiere.ad.trx.api.ITrxManager;
import org.adempiere.model.InterfaceWrapperHelper;
import org.adempiere.util.Check;
import org.adempiere.util.Services;
import org.compiere.util.TrxRunnable;

import de.metas.handlingunits.IHUAndItemsDAO;
import de.metas.handlingunits.model.I_M_HU;
import de.metas.handlingunits.model.I_M_HU_Item;
import de.metas.handlingunits.model.I_M_HU_PI_Item;

public final class HUAndItemsDAO implements IHUAndItemsDAO
{
	public static final transient HUAndItemsDAO instance = new HUAndItemsDAO();

	private HUAndItemsDAO()
	{
		super();
	}

	@Override
	public void saveHU(final I_M_HU hu)
	{
		final boolean isNew = hu.getM_HU_ID() <= 0;

		if (isNew)
		{
			HUItemsLocalCache.getCreate(hu)
					.setEmptyNotStaled();
		}

		// task 07600
		final String huTrxName = InterfaceWrapperHelper.getTrxName(hu);
		Services.get(ITrxManager.class).run(huTrxName, new TrxRunnable()
		{
			@Override
			public void run(final String localTrxName) throws Exception
			{
				InterfaceWrapperHelper.save(hu, localTrxName);
			}
		});

		//
		// If our HU was deactivated/destroyed => removed it from Parent's cache
		if (!hu.isActive())
		{
			final I_M_HU_Item parentHUItem = retrieveParentItem(hu);
			if (parentHUItem != null)
			{
				IncludedHUsLocalCache.getCreate(parentHUItem).removeItem(hu);
			}
		}
	}

	@Override
	public void delete(final I_M_HU hu)
	{
		final I_M_HU_Item parentHUItem = retrieveParentItem(hu);

		InterfaceWrapperHelper.delete(hu);

		if (parentHUItem != null)
		{
			IncludedHUsLocalCache.getCreate(parentHUItem).removeItem(hu);
		}
	}

	@Override
	public I_M_HU retrieveParent(final I_M_HU hu)
	{
		final I_M_HU_Item itemParent = hu.getM_HU_Item_Parent();
		if (itemParent == null)
		{
			return null;
		}
		return itemParent.getM_HU();
	}

	@Override
	public I_M_HU_Item retrieveParentItem(final I_M_HU hu)
	{
		final I_M_HU_Item parentHUItem = hu.getM_HU_Item_Parent();
		if (parentHUItem == null || parentHUItem.getM_HU_Item_ID() <= 0)
		{
			return null;
		}

		return parentHUItem;
	}

	@Override
	public void setParentItem(final I_M_HU hu, final I_M_HU_Item parentHUItem)
	{
		final I_M_HU_Item parentHUItemOld = retrieveParentItem(hu);

		hu.setM_HU_Item_Parent(parentHUItem);
		saveHU(hu);

		//
		// Caches update
		{
			if (parentHUItemOld != null)
			{
				IncludedHUsLocalCache.getCreate(parentHUItemOld).removeItem(hu);
			}
			if (parentHUItem != null)
			{
				IncludedHUsLocalCache.getCreate(parentHUItem).addItem(hu);
			}
		}

	}

	@Override
	public List<I_M_HU> retrieveIncludedHUs(final I_M_HU_Item item)
	{
		final IncludedHUsLocalCache includedHUsCache = IncludedHUsLocalCache.getCreate(item);
		return includedHUsCache.getItems();
	}

	@Override
	public List<I_M_HU_Item> retrieveItems(final I_M_HU hu)
	{
		final HUItemsLocalCache huItemsCache = HUItemsLocalCache.getCreate(hu);
		return huItemsCache.getItems();
	}

	@Override
	public I_M_HU_Item retrieveItem(final I_M_HU hu, final I_M_HU_PI_Item piItem)
	{
		final int piItemId = piItem.getM_HU_PI_Item_ID();
		for (final I_M_HU_Item item : retrieveItems(hu))
		{
			if (item.getM_HU_PI_Item_ID() == piItemId)
			{
				return item;
			}
		}

		return null;
	}

	@Override
	public I_M_HU_Item createHUItem(final I_M_HU hu, final I_M_HU_PI_Item piItem)
	{
		Check.assumeNotNull(hu, "hu not null");
		Check.assumeNotNull(piItem, "piItem not null");
		Check.assume(hu.getM_HU_PI_Version_ID() == piItem.getM_HU_PI_Version_ID(),
				"Incompatible HU's PI Version ({}) and Item PI Version ({})", hu, piItem);

		final I_M_HU_Item item = InterfaceWrapperHelper.newInstance(I_M_HU_Item.class, hu);
		item.setAD_Org_ID(hu.getAD_Org_ID());
		item.setM_HU(hu);
		item.setM_HU_PI_Item(piItem);

		// FIXME: this is making de.metas.customer.picking.service.impl.PackingServiceTest to fail
		// IncludedHUsLocalCache
		// .getCreate(item)
		// .setEmptyNotStaled();

		InterfaceWrapperHelper.save(item);

		//
		// Update HU Items cache
		HUItemsLocalCache
				.getCreate(hu)
				.addItem(item);

		return item;

	}
}
