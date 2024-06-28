# Discord Resource Channel Bot

This is a discord bot that turns markdown files into discord posts and keeps them in sync.

## Pre-requisites

- Markdown files
- Discord bot token
- Channels must already be created - the bot cannot currently manage channels

## How-to

### Create your folder structure

Create a 'data' directory, with subdirectories representing channels.

Within each channel directory, create a `channel.properties` file. The only required property is `channel_id`. For example:
```properties
# The discord channel ID. Enable developer mode in discord, and right click > Copy Channel ID
channel_id=12345678901234567890
# Create a table of contents at the top of the channel
table_of_contents_top=true
# Create a table of contents at the bottom of the channel
table_of_contents_bottom=true
```

Then, drop markdown files in the directory, with the name structure `xx_Title_Of_Post.md`, e.g. `01_My_First_Post.md`.
You can use any number of digits, but I suggest numbering your pages as 10, 20, 30,... so that you can insert other
posts between those without needing to rename the rest of the files.

The markdown files can contain relative links to other markdown files managed by the bot.

### Running

#### Via Docker

`docker run -v /path/to/data/:/app/data -e DISCORD_TOKEN='token here' ghcr.io/the-balance-ffxiv/discord-static-resource-bot:master`

#### Directly via JAR

Run the bot in the directory containing the `data` directory using Java 21.

`DISCORD_TOKEN='token here' java -jar /path/to/shaded-jar.jar`

### Building

#### Build Everything, Including Docker Image

`./gradlew buildImage`

This will build the image under the `resbot` name.

#### Build Manually

```shell
# Build JAR
./gradlew build
# Generate Dockerfile
./gradlew createDockerFile
# Build Docker image
./gradlew buildImage
# Alternative if you're having issues with the Gradle plugin talking to your Docker instance:
docker build -t resbot -f build/docker/Dockerfile .
```
