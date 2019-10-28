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
        <v-row>
            <v-col>
                <v-card>
                    <v-card-title>Обновить БД</v-card-title>
                    <v-card-actions class="ml-5">
                        <form enctype="multipart/form-data">
                            <v-row><input type="file" name="file" v-on:change="fileChange($event.target.files)" /></v-row>
                            <v-row><v-btn color="success" v-on:click="uploadExcelFile()">Загрузить</v-btn></v-row>
                        </form>
                    </v-card-actions>
                </v-card>
            </v-col>
            <v-col>
                <v-card>
                    <v-card-title>Doffler-Leran</v-card-title>
                    <v-card-actions class="ml-5">
                        <form enctype="multipart/form-data">
                            <v-row><input type="file" name="fileBrands" v-on:change="brandsFileChange($event.target.files)" /></v-row>
                            <v-row><v-btn color="success" v-on:click="uploadBrandsPrice()">Загрузить бренд-прайс</v-btn></v-row>
                        </form>
                    </v-card-actions>
                </v-card>
            </v-col>
        </v-row>
        <v-row>
            <v-col>
                <v-card class="mt-3">
                    <v-card-title>Обновить каталог</v-card-title>
                    <v-card-actions class="ml-5">
                        <v-btn color="blue" v-on:click="updateCatalog()">Обновить</v-btn>
                    </v-card-actions>
                </v-card>
            </v-col>
            <v-col>
                <v-card class="mt-3">
                    <v-card-title>Спарсить ссылки фото RUSBT</v-card-title>
                    <v-card-actions class="ml-5">
                        <v-btn color="yellow" v-on:click="parsRUSBTPics()">Запустить парсер</v-btn>
                    </v-card-actions>
                </v-card>
            </v-col>
        </v-row>
        <v-row>
            <v-col>
                <v-card class="mt-3">
                    <v-card-title>Test</v-card-title>
                    <v-card-actions class="ml-5">
                        <v-btn color="red" v-on:click="test()">BANG</v-btn>
                    </v-card-actions>
                </v-card>
            </v-col>
        </v-row>
        <v-row>
            <v-col>
                <v-card class="mt-3">
                    <v-card-title>Test User Auth</v-card-title>
                    <v-card-actions class="ml-5">
                        <v-btn color="red" v-on:click="testUserAuth()">test</v-btn>
                    </v-card-actions>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
    import axios from "axios";
    export default {
        data() {
            return {
                file: new FormData(),
                fileBrands: new FormData(),
            }
        },
        methods: {
            fileChange(fileList) {
                this.file.append("file", fileList[0], fileList[0].name);
            },
            brandsFileChange(fileList) {
                this.fileBrands.append("fileBrands", fileList[0], fileList[0].name);
            },
            uploadExcelFile() {
                axios.post('api/admin/uploadFileDB', this.file).then(this.file = new FormData());
            },
            uploadBrandsPrice() {
                axios.post('api/admin/uploadFileBrands', this.fileBrands).then(console.log('brands uploaded'));
            },
            updateCatalog() {
                axios.post('api/admin/updateCatalog').then(console.log('catalog updated'));
            },
            parsRUSBTPics() {
                axios.post('api/admin/parsePicsRUSBT').then(console.log('pics parsed'));
            },
            test() {
                axios.post('api/admin/test').then(console.log('OK'));
            },
            testUserAuth() {
                console.log(this.$store.state.currentUser)
            }
        }
    }
</script>

<style scoped></style>