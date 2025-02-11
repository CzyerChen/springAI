## 模型展示交互平台 Open Web UI
                 
## 如何安装

Installation via Python pip 🐍
Open WebUI can be installed using pip, the Python package installer. Before proceeding, ensure you're using` Python 3.11` to avoid compatibility issues.

Install Open WebUI: Open your terminal and run the following command to install Open WebUI:

pip install open-webui
Running Open WebUI: After installation, you can start Open WebUI by executing:

open-webui serve

```bash
$ open-webui serve
Loading WEBUI_SECRET_KEY from file, not provided as an environment variable.
Generating a new secret key and saving it to /Users/chenzy/.webui_secret_key
Loading WEBUI_SECRET_KEY from /Users/chenzy/.webui_secret_key
/usr/local/lib/python3.11/site-packages/open_webui
/usr/local/lib/python3.11/site-packages
/usr/local/lib/python3.11
Running migrations
INFO  [alembic.runtime.migration] Context impl SQLiteImpl.
INFO  [alembic.runtime.migration] Will assume non-transactional DDL.
INFO  [alembic.runtime.migration] Running upgrade  -> 7e5b5dc7342b, init
INFO  [alembic.runtime.migration] Running upgrade 7e5b5dc7342b -> ca81bd47c050, Add config table
INFO  [alembic.runtime.migration] Running upgrade ca81bd47c050 -> c0fbf31ca0db, Update file table
INFO  [alembic.runtime.migration] Running upgrade c0fbf31ca0db -> 6a39f3d8e55c, Add knowledge table
Creating knowledge table
Migrating data from document table to knowledge table
INFO  [alembic.runtime.migration] Running upgrade 6a39f3d8e55c -> 242a2047eae0, Update chat table
Converting 'chat' column to JSON
Renaming 'chat' column to 'old_chat'
Adding new 'chat' column of type JSON
Dropping 'old_chat' column
INFO  [alembic.runtime.migration] Running upgrade 242a2047eae0 -> 1af9b942657b, Migrate tags
INFO  [alembic.runtime.migration] Running upgrade 1af9b942657b -> 3ab32c4b8f59, Update tags
Primary Key: {'name': None, 'constrained_columns': []}
Unique Constraints: [{'name': 'uq_id_user_id', 'column_names': ['id', 'user_id']}]
Indexes: [{'name': 'tag_id', 'column_names': ['id'], 'unique': 1, 'dialect_options': {}}]
Creating new primary key with 'id' and 'user_id'.
Dropping unique constraint: uq_id_user_id
Dropping unique index: tag_id
INFO  [alembic.runtime.migration] Running upgrade 3ab32c4b8f59 -> c69f45358db4, Add folder table
INFO  [alembic.runtime.migration] Running upgrade c69f45358db4 -> c29facfe716b, Update file table path
INFO  [alembic.runtime.migration] Running upgrade c29facfe716b -> af906e964978, Add feedback table
INFO  [alembic.runtime.migration] Running upgrade af906e964978 -> 4ace53fd72c8, Update folder table and change DateTime to BigInteger for timestamp fields
INFO  [alembic.runtime.migration] Running upgrade 4ace53fd72c8 -> 922e7a387820, Add group table
INFO  [alembic.runtime.migration] Running upgrade 922e7a387820 -> 57c599a3cb57, Add channel table
INFO  [alembic.runtime.migration] Running upgrade 57c599a3cb57 -> 7826ab40b532, Update file table
INFO  [alembic.runtime.migration] Running upgrade 7826ab40b532 -> 3781e22d8b01, Update message & channel tables
INFO  [open_webui.env] 'DEFAULT_LOCALE' loaded from the latest database entry
INFO  [open_webui.env] 'DEFAULT_PROMPT_SUGGESTIONS' loaded from the latest database entry
WARNI [open_webui.env] 

WARNING: CORS_ALLOW_ORIGIN IS SET TO '*' - NOT RECOMMENDED FOR PRODUCTION DEPLOYMENTS.

INFO  [open_webui.env] Embedding model set: sentence-transformers/all-MiniLM-L6-v2
/usr/local/lib/python3.11/site-packages/pydub/utils.py:170: RuntimeWarning: Couldn't find ffmpeg or avconv - defaulting to ffmpeg, but may not work
  warn("Couldn't find ffmpeg or avconv - defaulting to ffmpeg, but may not work", RuntimeWarning)
WARNI [langchain_community.utils.user_agent] USER_AGENT environment variable not set, consider setting it to identify your requests.


  ___                    __        __   _     _   _ ___
 / _ \ _ __   ___ _ __   \ \      / /__| |__ | | | |_ _|
| | | | '_ \ / _ \ '_ \   \ \ /\ / / _ \ '_ \| | | || |
| |_| | |_) |  __/ | | |   \ V  V /  __/ |_) | |_| || |
 \___/| .__/ \___|_| |_|    \_/\_/ \___|_.__/ \___/|___|
      |_|


v0.5.10 - building the best open-source AI user interface.

https://github.com/open-webui/open-webui

1_Pooling/config.json: 100%|████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 190/190 [00:00<00:00, 655kB/s]
modules.json: 100%|█████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 349/349 [00:00<00:00, 925kB/s]
.gitattributes: 100%|██████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 1.23k/1.23k [00:00<00:00, 4.11MB/s]
config_sentence_transformers.json: 100%|████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 116/116 [00:00<00:00, 599kB/s]
config.json: 100%|█████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 612/612 [00:00<00:00, 2.22MB/s]
data_config.json: 100%|█████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 39.3k/39.3k [00:00<00:00, 851kB/s]
README.md: 100%|███████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 10.7k/10.7k [00:00<00:00, 29.5MB/s]
model_O4.onnx: 100%|███████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 45.2M/45.2M [00:07<00:00, 6.05MB/s]
model_qint8_arm64.onnx: 100%|██████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 23.0M/23.0M [00:08<00:00, 2.74MB/s]
openvino/openvino_model.xml: 100%|████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 211k/211k [00:00<00:00, 661kB/s]
openvino_model_qint8_quantized.bin: 100%|██████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 22.9M/22.9M [00:04<00:00, 4.85MB/s]
(…)nvino/openvino_model_qint8_quantized.xml: 100%|████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 368k/368k [00:00<00:00, 590kB/s]
model_quint8_avx2.onnx: 100%|██████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 23.0M/23.0M [00:13<00:00, 1.67MB/s]
pytorch_model.bin: 100%|███████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 90.9M/90.9M [01:08<00:00, 1.34MB/s]
sentence_bert_config.json: 100%|██████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 53.0/53.0 [00:00<00:00, 227kB/s]
special_tokens_map.json: 100%|██████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 112/112 [00:00<00:00, 612kB/s]
model_O2.onnx: 100%|████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 90.3M/90.3M [01:35<00:00, 942kB/s]
model_O1.onnx: 100%|████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 90.4M/90.4M [01:36<00:00, 934kB/s]
tokenizer.json: 100%|████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 466k/466k [00:00<00:00, 1.61MB/s]
train_script.py: 100%|█████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 13.2k/13.2k [00:00<00:00, 14.4MB/s]
tokenizer_config.json: 100%|███████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 350/350 [00:00<00:00, 2.12MB/s]
vocab.txt: 100%|█████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 232k/232k [00:00<00:00, 1.00MB/s]
openvino_model.bin: 100%|██████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 90.3M/90.3M [01:28<00:00, 1.02MB/s]
rust_model.ot: 100%|███████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 90.9M/90.9M [01:18<00:00, 1.16MB/s]
model.onnx: 100%|███████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 90.4M/90.4M [01:43<00:00, 872kB/s]
model_O3.onnx: 100%|████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 90.3M/90.3M [01:43<00:00, 872kB/s]
model.safetensors: 100%|████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 90.9M/90.9M [01:47<00:00, 846kB/s]
tf_model.h5: 100%|█████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 91.0M/91.0M [00:24<00:00, 3.78MB/s]
Fetching 30 files: 100%|████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 30/30 [01:53<00:00,  3.79s/it]
INFO:     Started server process [41445]███████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 90.9M/90.9M [01:47<00:00, 1.57MB/s]
INFO:     Waiting for application startup.█████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 90.3M/90.3M [01:43<00:00, 1.35MB/s]
INFO:     Application startup complete.█████████████▎                                                                                                                  | 21.0M/91.0M [00:15<00:47, 1.47MB/s]
INFO:     Uvicorn running on http://0.0.0.0:8080 (Press CTRL+C to quit)
tf_model.h5:  46%|████████████████████████████████████████████████████████████████████▋                                                                                | 41.9M/91.0M [00:19<00:16, 2.89MB/s]
tf_model.h5: 100%|█████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████| 91.0M/91.0M [00:24<00:00, 7.60MB/s]
```
This will start the Open WebUI server, which you can access at http://localhost:8080
                              
## 此外docker K8S的方式更适合正式的场合或者是生产

```bash
docker run -d -p 3000:8080 --add-host=host.docker.internal:host-gateway -v open-webui:/app/backend/data --name open-webui --restart always ghcr.io/open-webui/open-webui:main
```


