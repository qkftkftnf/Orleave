<template>
  <div class="chat">
    <q-btn icon="arrow_back_ios" class="chat-btn">
      <q-btn
        icon="arrow_forward_ios"
        v-show="showChat === true"
        class="chat-hide-btn"
        transition-show="slide-left"
        transition-hide="slide-right"
      />
      <q-menu
        v-model="showChat"
        class="chat-bar"
        anchor="center right"
        self="center right"
        persistent
      >
        <div class="chat-area">
          <q-scroll-area class="chat-log" ref="chatScroll">
            <q-chat-message
              v-for="chat in chatLog"
              :key="chat"
              :name="chat.nickname"
              :text="[chat.text]"
              :sent="chat.sent"
              :stamp="chat.time"
            />
          </q-scroll-area>
          <div class="row justify-center items-center chat-input-div">
            <q-input
              class="chat-input"
              v-model="message"
              bg-color="white"
              borderless
              dense
              type="text"
              @keyup.enter="sendMessage"
            >
              <template v-slot:append>
                <q-btn
                  color="primary"
                  @click="sendMessage"
                  label="전송"
                  size="10px"
                  dense
                />
              </template>
            </q-input>
          </div>
        </div>
      </q-menu>
    </q-btn>
  </div>
</template>

<script>
import { ref } from 'vue'
import Stomp from 'webstomp-client'
import SockJS from 'sockjs-client'
import jwtDecode from 'jwt-decode'
import { WEBSOCKET_URL } from '@/config'
import { mapState } from 'vuex'

export default {
  setup() {
    const message = ref('')
    const showChat = ref(false)
    const popupMatching = ref(false)
    const popupReport = ref(false)
    const reportSelected = ref([])
    const reportMsg = ref('')
    const chatMsg = ref(null)
    const recvList = ref([])
    const chatLog = ref([])
    const userName = ref('')
    const scrollArea = ref(null)
    const scrollTarget = ref(null)
    return {
      userName,
      message,
      recvList,
      showChat,
      chatLog,
      reportOptions: [
        { label: '폭언 및 욕설', value: 0 },
        { label: '부적절한 닉네임', value: 1 },
        { label: '부적절한 언행', value: 2 },
        { label: '성희롱 및 성추행', value: 3 },
        { label: '혐오발언', value: 4 },
        { label: '기타', value: 5 }
      ],
      reportSelected,
      reportMsg,
      chatMsg,
      popupMatching,
      popupReport,
      scrollArea,
      scrollTarget,
      opponent: '감자튀김'
    }
  },
  created() {
    this.userName = jwtDecode(sessionStorage.getItem('Authorization')).NickName
    this.connect()
  },
  computed: {
    ...mapState('meetingStore', ['sessionId'])
  },
  methods: {
    sendMessage(e) {
      if (this.message !== '') {
        this.send()
        this.message = ''
      }
    },
    send() {
      if (this.stompClient && this.stompClient.connected) {
        const date = new Date()
        const hour =
          date.getHours() < 10 ? '0' + date.getHours() : date.getHours()
        const minute =
          date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()
        const second =
          date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds()
        const msg = {
          nickname: this.userName,
          content: this.message,
          time: hour + ':' + minute + ':' + second
        }
        this.stompClient.send(
          '/pub/chat/' + this.sessionId,
          JSON.stringify(msg),
          {}
        )
      }
    },
    connect() {
      const serverURL = WEBSOCKET_URL
      const socket = new SockJS(serverURL)
      this.stompClient = Stomp.over(socket)
      this.stompClient.connect(
        {},
        (frame) => {
          // 소켓 연결 성공
          this.connected = true
          // 서버의 메시지 전송 endpoint를 구독합니다.
          // 이런형태를 pub sub 구조라고 합니다.
          this.stompClient.subscribe('/sub/chat/' + this.sessionId, (res) => {
            // 받은 데이터를 json으로 파싱하고 리스트에 넣어줍니다.
            this.recvList.push(JSON.parse(res.body))
            const temp = this.recvList.pop()
            if (temp.nickname === this.userName) {
              this.chatLog.push({
                nickname: temp.nickname,
                text: temp.content,
                time: temp.time,
                sent: true
              })
            } else {
              this.chatLog.push({
                nickname: temp.nickname,
                text: temp.content,
                time: temp.time,
                sent: false
              })
            }
            this.scrollArea = this.$refs.chatScroll
            this.scrollTarget = this.scrollArea.getScrollTarget()
            const duration = 100 // ms - use 0 to instant scroll
            this.scrollArea.setScrollPosition(
              'vertical',
              this.scrollTarget.scrollHeight,
              duration
            )
          })
        },
        () => {
          this.connected = false
        }
      )
    }
  }
}
</script>

<style scoped>
.chat {
  overflow: hidden;
}
.chat-btn {
  position: fixed;
  right: -10px;
  top: 45%;
  width: 10px;
  height: 50px;
  background: #f3f1eb;
}
.chat-hide-btn {
  width: 10px;
  height: 50px;
  position: absolute;
  left: -255px;
  z-index: 10;
  background: #f3f1eb;
}
.chat-bar {
  position: relative;
  right: 10px;
  width: 200px;
}
.chat-area {
  background-color: #e5edb8 !important;
}
.chat-log {
  height: 350px;
  width: 250px;
  padding: 20px;
  overflow-x: hidden;
}
.chat-input-div {
  width: 250px;
  background-color: white;
}
.chat-input {
  width: 230px;
}
.chat-input input {
  padding: 6px 10px !important;
}
.popup {
  background: #f3f1eb;
  width: 20%;
  min-width: 350px;
  max-width: 500px;
}
.popup-bar {
  background: #b3a286;
}
.popup-text {
  font-size: 150%;
}
.report-popup {
  background: #f3f1eb;
  width: 30%;
  min-width: 450px;
  max-width: 550px;
}
</style>
