# Audio API Endpoints

## `POST /api/v1/audio/upload`

Uploads an audio file to the server for processing.

### Request

**Content Type:** `multipart/form-data`

The form data body must contain the following fields:

| Field | Type | Required | Description |
| --- | --- | --- | --- |
| `File` | File | Yes | The file to upload. The file must not be empty. |
| `resource` | String | Yes | The resource associated with the upload. This value may only be empty when `isLive` is `false`. |
| `isLive` | Boolean | Yes | Indicates whether the uploaded audio is associated with a live stream. |

### Validation

- `File` must be provided and must not be empty.
- `resource` must be provided.
- `isLive` must be provided.
- If `isLive` is `true`, `resource` must not be empty.
- If `isLive` is `false`, `resource` may be empty.

### Responses

| Status | Description |
| --- | --- |
| `202 Accepted` | The request was accepted and the server is processing the submitted item(s). |
| `400 Bad Request` | The request is missing required information or does not satisfy the validation requirements above. |

---

## `GET /api/v1/audio/live`

Retrieves the playlist for the current live audio stream.

### Request

No request body is required.

### Responses

| Status | Description |
| --- | --- |
| `200 OK` | Returns the `.m3u8` playlist file for the current audio stream. |
| `204 No Content` | The `.m3u8` playlist file is not available. |

---

## `GET /api/v1/audio/{file}`

Retrieves a requested audio file by filename.

### Path Parameters

| Parameter | Type | Required | Description |
| --- | --- | --- | --- |
| `file` | String | Yes | The filename to retrieve. The server uses this value to search for the requested file. |

### Request

The requested filename must be supplied in the `{file}` path parameter.

**Example:**

```text
GET /api/v1/audio/example.ts
```

### Responses

| Status | Description |
| --- | --- |
| `200 OK` | Returns the requested file. |
| `204 No Content` | The requested file is not available. |

---

## `GET /api/v1/audio/{resource}/{file}`

Retrieves a requested audio file from a specified resource path.

### Path Parameters

| Parameter | Type | Required | Description |
| --- | --- | --- | --- |
| `resource` | String | Yes | The resource path used to locate the file. It must not contain file separators. |
| `file` | String | Yes | The filename to retrieve within the specified resource. |

### Request

Both the resource and filename must be supplied as path parameters.

The `{resource}` value must not contain file separators.

**Example:**

```text
GET /api/v1/audio/example-resource/example.ts
```

### Responses

| Status | Description |
| --- | --- |
| `200 OK` | Returns the requested file from the specified resource. |
| `204 No Content` | The requested file is not available. |
