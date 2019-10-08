<template lang="html">
    <v-container>
        <!--<div class="centerx">
            <vs-upload action="/api/admin/uploadFileDB" @on-success="successUpload" />
        </div>

        <div>
            <v-file-input v-model="files" color="deep-purple accent-4" counter label="File input" multiple placeholder="Select your files" prepend-icon="mdi-paperclip" outlined :show-size="1000">
                <template v-slot:selection="{ index, text }">
                    <v-chip v-if="index < 2" color="deep-purple accent-4" dark label small>
                        {{ text }}
                    </v-chip>
                    <span v-else-if="index === 2" class="overline grey&#45;&#45;text text&#45;&#45;darken-3 mx-2">
                        +{{ files.length - 2 }} File(s)
                    </span>
                </template>
            </v-file-input>
        </div>

        <form id="uploadForm" name="uploadForm" enctype="multipart/form-data">
            <input type="file" id="files" name="files" multiple><br>
            <input type="text" id="name" name="name"><br>
            <input type="email" id="email" name="email">
            <input type=button value=Upload @click="this.uploadFiles">
        </form>-->

        <!--<form id="uploadForm" name="uploadForm" enctype="multipart/form-data">
            <v-file-input id="file" name="file" label="Файл Excel" outlined dense></v-file-input>
            <v-btn color="success" value=Upload @click="uploadFiles">Загрузить</v-btn>
        </form>-->

        <div>
            <h1>Обновить БД</h1>
            <form enctype="multipart/form-data">
                <v-row><input type="file" name="file" v-on:change="fileChange($event.target.files)" /></v-row>
                <v-row><v-btn color="success" v-on:click="upload()">Загрузить</v-btn></v-row>
            </form>
        </div>
    </v-container>
</template>

<script>
    import axios from "axios";

    export default {
        data() {
            return {
                file: new FormData()
            }
        },
        methods: {
            fileChange(fileList) {
                this.file.append("file", fileList[0], fileList[0].name);
            },
            upload() {
                axios.post('api/admin/uploadFileDB', this.file).then(response =>{
                    console.log('ok')
                });
            }
        }
    }
</script>

<style scoped></style>