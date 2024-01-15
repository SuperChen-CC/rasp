package org.javaweb.rasp.commons.utils;

import org.javaweb.rasp.commons.gson.Gson;
import org.javaweb.rasp.commons.gson.GsonBuilder;
import org.javaweb.rasp.commons.gson.JsonSyntaxException;
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

import static org.javaweb.rasp.commons.log.RASPLogger.errorLog;

/**
 * Created by yz on 2017/2/20.
 *
 * @author yz
 */
public class JsonUtils {

	private static final int MAX_LEN = 10000;

	private static final JsonObjectTypeAdapter TYPE_ADAPTER = new JsonObjectTypeAdapter();

	public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().
			registerTypeAdapter(List.class, TYPE_ADAPTER).
			registerTypeAdapter(Map.class, TYPE_ADAPTER).
			create();

	public static String toJson(Object src) {
		return GSON.toJson(src);
	}

	public static Map<String, Object> toJsonMap(String jsonStr) {
		if (jsonStr != null) {
			Map<String, Object> map = null;

			try {
				map = GSON.fromJson(jsonStr, new TypeToken<Map<String, Object>>() {
				}.getType());
			} catch (JsonSyntaxException e) {
				addErrorLog(jsonStr, e);
			}

			if (map != null) return map;
		}

		return new HashMap<String, Object>();
	}

	public static List<Map<String, Object>> toJsonList(String jsonStr) {
		if (jsonStr != null) {
			List<Map<String, Object>> list = null;
			try {
				list = GSON.fromJson(jsonStr, new TypeToken<List<Map<String, Object>>>() {
				}.getType());
			} catch (JsonSyntaxException e) {
				addErrorLog(jsonStr, e);
			}

			if (list != null) return list;
		}

		return new ArrayList<Map<String, Object>>();
	}

	public static <T> T fromJson(String jsonStr, TypeToken<T> t) {
		if (jsonStr != null) {
			try {
				return GSON.fromJson(jsonStr, t.getType());
			} catch (JsonSyntaxException e) {
				addErrorLog(jsonStr, e);
			}
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

	private static void addErrorLog(String jsonStr, Exception e) {
		if (jsonStr != null && jsonStr.length() > MAX_LEN) jsonStr = jsonStr.substring(0, MAX_LEN - 1);

		errorLog("JSON：" + jsonStr + "解析失败，语法错误", e);
	}

}
