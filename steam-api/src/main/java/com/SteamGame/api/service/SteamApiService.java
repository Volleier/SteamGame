package com.SteamGame.api.service;

import java.io.IOException;

/**
 * Steam Web API相关操作的服务接口（位于steam-api模块内）。
 *
 * 核心职责：通过调用轻量级接口（ISteamWebAPIUtil/GetSupportedAPIList/v1）验证Steam Web API密钥，
 * 并解析响应。实现类应尽量少做解析，并在需要时返回原始数据，由调用者决定如何处理结果。
 */
public interface SteamApiService {

	/**
	 * 调用ISteamWebAPIUtil/GetSupportedAPIList/v1接口，返回原始JSON响应体。
	 *
	 * @param apiKey 用于验证的Steam Web API密钥
	 * @return 原始JSON响应体（网络错误时可能为null）
	 */
	String getSupportedApiList(String apiKey) throws IOException, InterruptedException;

	/**
	 * 验证提供的API密钥是否有效。
	 *
	 * 当HTTP请求返回200 OK且响应包含"apilist"字段时，认为密钥有效。
	 * 对于无效密钥（包括网络错误或非200状态码），返回false。
	 *
	 * @param apiKey 要验证的API密钥
	 * @return 密钥有效返回true，否则返回false
	 */
	boolean isApiKeyValid(String apiKey) throws IOException, InterruptedException;


	/**
     * 调用 IPlayerService/GetOwnedGames/v1 并返回原始 JSON 响应
     *
     * @param steamId 64位Steam用户ID
     * @param apiKey  API密钥
     * @return 原始 JSON 响应作为字符串
     */
    String getOwnedGames(String steamId, String apiKey) throws IOException, InterruptedException;
}
