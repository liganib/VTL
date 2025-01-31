/*
 * Copyright © 2020 Banca D'Italia
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
 */
package it.bancaditalia.oss.vtl.impl.transform.aggregation;

import java.io.Serializable;

public interface AnalyticTransformation
{
	public enum OrderingMethod 
	{
		ASC, DESC
	};

	public static class OrderByItem implements Serializable
	{
		private static final long serialVersionUID = 1L;
		private final String name;
		private final OrderingMethod method;

		public OrderByItem(String name, OrderingMethod method)
		{
			this.name = name;
			this.method = method;
		}

		public String getName()
		{
			return name;
		}

		public OrderingMethod getMethod()
		{
			return method;
		}
		
		@Override
		public String toString()
		{
			return (method != null ? method + " " : "") + name;
		}
	}
}
