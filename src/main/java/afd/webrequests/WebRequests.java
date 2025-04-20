package afd.webrequests;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import static net.minecraft.server.command.CommandManager.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;

public class WebRequests implements ModInitializer {
	public static final String MOD_ID = "web-requests";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		@SuppressWarnings("unused")
        HttpClient httpClient = HttpClient.newHttpClient();
		LOGGER.info("Loaded Web Requests Mod");
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess,
				environment) -> dispatcher.register(literal("request")
						.then(literal("get").then(argument("URL", StringArgumentType.string()).then(
								argument("headers", StringArgumentType.string()).executes(WebRequests::getRequest))))
						.then(literal("post")
						.then(argument("URL",StringArgumentType.string())
						.then(argument("headers",StringArgumentType.string()))
						.then(argument("body",StringArgumentType.string())))
						.executes(WebRequests::postRequest))));
						
					};
	


	public static int postRequest(CommandContext<ServerCommandSource> context){
		String url = StringArgumentType.getString(context, "URL");
		String headers = StringArgumentType.getString(context, "headers");
		String body = StringArgumentType.getString(context,"body");
		try {
			HttpRequest.Builder builder = HttpRequest.newBuilder()
					.setHeader("User-Agent","Minecraft Fabric Web Requests mod by AFancyDog [https://github.com/AFancyDog/Web-Requests]")
					.POST(HttpRequest.BodyPublishers.ofString(body))
					.uri(new URI(url));
			// "Header: content goes here:::X-Other-header: some stuff" splits into two
			// headers
			String[] header_list = headers.split(":::");
			for (String s : header_list) {
				String[] kv = s.split(":", 2);
				builder.header(kv[0], kv[1]);
			}
			HttpRequest request = builder.build();
			HttpResponse<String> response = HttpClient.newBuilder()
					.build()
					.send(request, BodyHandlers.ofString());
			LOGGER.info("Response is %s".formatted(response.statusCode()));
			return response.statusCode();
		} catch (Exception error) {
			LOGGER.error("Get request failed with exception: %s".formatted(error));
		}
			




		return 1;
	}




	public static int getRequest(CommandContext<ServerCommandSource> context) {
		String url = StringArgumentType.getString(context, "URL");
		String headers = StringArgumentType.getString(context, "headers");
		
		try {
			HttpRequest.Builder builder = HttpRequest.newBuilder()
					.setHeader("User-Agent","Minecraft Fabric Web Requests mod by AFancyDog [https://github.com/AFancyDog/Web-Requests]")
					.GET()
					.uri(new URI(url));
			// "Header: content goes here:::X-Other-header: some stuff" splits into two
			// headers
			String[] header_list = headers.split(":::");
			for (String s : header_list) {
				String[] kv = s.split(":", 2);
				builder.header(kv[0], kv[1]);
			}
			HttpRequest request = builder.build();
			HttpResponse<String> response = HttpClient.newBuilder()
					.build()
					.send(request, BodyHandlers.ofString());
			context.getSource().sendFeedback(() -> Text.literal("Sent a get request to %s".formatted(url)),false);
			LOGGER.info("Response from %s".formatted(url) + "is %s".formatted(response.statusCode()));
			return response.statusCode();
		} catch (Exception error) {
			LOGGER.error("Get request failed with exception: %s".formatted(error));
		}
		return 1;

	}
}
