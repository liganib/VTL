/*******************************************************************************
 * Copyright 2020, Bank Of Italy
 *
 * Licensed under the EUPL, Version 1.2 (the "License");
 * You may not use this work except in compliance with the
 * License.
 * You may obtain a copy of the License at:
 *
 * https://joinup.ec.europa.eu/sites/default/files/custom-page/attachment/2020-03/EUPL-1.2%20EN.txt
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the License is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 *
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
package it.bancaditalia.oss.vtl.config;

import static it.bancaditalia.oss.vtl.config.ConfigurationManager.VTLProperty.CONFIG_MANAGER;

import it.bancaditalia.oss.vtl.exceptions.VTLNestedException;

public class ConfigurationManagerFactory
{
	private final ConfigurationManager config;
	
	public ConfigurationManagerFactory()
	{
		try
		{
			config = Class.forName(CONFIG_MANAGER.getValue())
					.asSubclass(ConfigurationManager.class)
					.newInstance();
		}
		catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
		{
			throw new VTLNestedException("Error loading configuration", e);
		}
	}
	
	public ConfigurationManager getDefault() 
	{
		return config;
	} 
}
