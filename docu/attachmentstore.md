# AttachmentStore

<!-- MarkdownTOC -->

- [Description](#desription)
- [Functionality](#functionality)
- [AttachmentService](#attachmentService)

<!-- /MarkdownTOC -->

## Description

The AttachmentStore represents a Service to persist Data in any variation. For example: an audio-file from a messenger need to be stored locally to be processed further. 

## Functionality

* storing voice/audio files locally
** you can give FileURIs or ByteArrayOutputStreams
* create and return file pathes (local)
* create and return FileURIs for GET requests

## AttachmentService

* the AttachmentService is simple REST Service to provide the data upload via GET requests.