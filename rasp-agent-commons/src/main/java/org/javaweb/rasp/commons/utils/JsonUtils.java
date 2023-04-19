package org.javaweb.rasp.commons.utils;

import org.javaweb.rasp.commons.gson.Gson;
import org.javaweb.rasp.commons.gson.GsonBuilder;
import org.javaweb.rasp.commons.gson.TypeAdapter;
import org.javaweb.rasp.commons.gson.internal.LinkedTreeMap;
import org.javaweb.rasp.commons.gson.reflect.TypeToken;
import org.javaweb.rasp.commons.gson.stream.JsonReader;
import org.javaweb.rasp.commons.gson.stream.JsonToken;
import org.javaweb.rasp.commons.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yz on 2017/2/20.
 *
 * @author yz
 */
public class JsonUtils {

	private static final JsonObjectTypeAdapter TYPE_ADAPTER = new JsonObjectTypeAdapter();

	public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().
			registerTypeAdapter(List.class, TYPE_ADAPTER).
			registerTypeAdapter(Map.class, TYPE_ADAPTER).
			create();

	public static String toJson(Object src) {
		return GSON.toJson(src);
	}

	public static Map<String, Object> toJsonMap(String object) {
		if (object != null) {
			return GSON.fromJson(object, new TypeToken<Map<String, Object>>() {
			}.getType());
		}

		return new HashMap<String, Object>();
	}

	public static List<Map<String, Object>> toJsonList(String jsonStr) {
		if (jsonStr != null) {
			return GSON.fromJson(jsonStr, new TypeToken<List<Map<String, Object>>>() {
			}.getType());
		}

		return new ArrayList<Map<String, Object>>();
	}

	public static <T> T fromJson(String jsonStr, TypeToken<T> t) {
		if (jsonStr != null) {
			return GSON.fromJson(jsonStr, t.getType());
		}

		return null;
	}

	/**
	 * 适配JSON序列化数据类型:<a href="https://stackoverflow.com/questions/36508323/how-can-i-prevent-gson-from-converting-integers-to-doubles">How can I prevent gson from converting integers to doubles</a>
	 */
	public static final class JsonObjectTypeAdapter extends TypeAdapter<Object> {

		private final TypeAdapter<Object> delegate = new Gson().getAdapter(Object.class);

		@Override
		public Object read(JsonReader in) throws IOException {
			JsonToken token = in.peek();

			switch (token) {
				case BEGIN_ARRAY:
					List<Object> list = new ArrayList<Object>();
					in.beginArray();

					while (in.hasNext()) {
						list.add(read(in));
					}

					in.endArray();
					return list;

				case BEGIN_OBJECT:
					Map<String, Object> map = new LinkedTreeMap<String, Object>();
					in.beginObject();

					while (in.hasNext()) {
						map.put(in.nextName(), read(in));
					}

					in.endObject();
					return map;

				case STRING:
					return in.nextString();

				case NUMBER:
					Number num = in.nextDouble();

					if (Math.ceil(num.doubleValue()) == num.longValue())
						return num.longValue();
					else {
						return num.doubleValue();
					}

				case BOOLEAN:
					return in.nextBoolean();

				case NULL:
					in.nextNull();
					return null;

				default:
					throw new IllegalStateException();
			}
		}

		@Override
		public void write(JsonWriter out, Object value) throws IOException {
			if (value == null) {
				out.nullValue();

				return;
			}

			delegate.write(out, value);
		}

	}

}
