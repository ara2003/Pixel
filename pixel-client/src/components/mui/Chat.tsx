import * as React from 'react';
import {SetStateAction, useEffect, useState} from 'react';
import Box from '@mui/material/Box';
import AppBar from '@mui/material/AppBar';
import Typography from '@mui/material/Typography';
import {
    Button,
    Divider,
    Drawer,
    IconButton,
    List,
    ListItem,
    ListItemAvatar,
    ListItemText,
    Skeleton,
    Toolbar
} from "@mui/material";
import {useAsync, useChatTitle, useMeUser, useOneRoom, useRoomUsers} from "../HookUtil";
import {UserAvatar, UserIdInfo, UserName} from "../user/User";
import {getRoomUsers, leaveRoom, Room, UserChanelRoleAttachmentRole, UserRoleAttachment} from "../../api/data/Room";
import './Chat.css'
import MenuIcon from '@mui/icons-material/Menu';
import {Send} from '@mui/icons-material';
import {createImageMessage, createTextMessage, getOneMessage, replaceTextMessage} from "../../api/data/Message";
import {Messages} from "./Messages";
import DragDrop from '../DragDrop';
import {uploadFile} from "../../api/Files";
import HideIf from "../HideIf";
import {User} from "../../api/data/User";
import {CLIENT_URI} from "../../api/FetchUtil";

async function getCanWrite(room: Room | undefined, user: User | undefined) {
    if (!room)
        return false
    if (!user)
        return false
    const attachment = await getAttachment(room, user)
    if (attachment.type === 'channel') {
        if (attachment.role === UserChanelRoleAttachmentRole.PRIVATE_USER || attachment.role === UserChanelRoleAttachmentRole.PUBLIC_USER) {
            return false
        }
    }
    return true
}

async function getAttachment(room: Room, user: User) {
    const users = await getRoomUsers(room.id)
    for (const attachment of users) {
        if (attachment.user === user.id)
            return attachment
    }
    throw new Error()
}

export function Chat({chatId}: { chatId: number }) {
    const [editMessageId, setEditMessageId] = useState<number>()
    const [text, setText] = useState('')
    const chat = useOneRoom(chatId)
    const chatTitle = useChatTitle(chat)
    const me = useMeUser()
    const [open, setOpen] = React.useState(false);
    const [file, setFile] = useState<File>()
    const canWrite = useAsync(() => getCanWrite(chat, me), [chat, me]) && true

    const editMessage = useAsync(() => editMessageId
            ? getOneMessage(editMessageId)
            : Promise.resolve(undefined)
        , [editMessageId])

    useEffect(() => {
        if (editMessage && editMessage.type === 'text')
            setText(editMessage.content)
    }, [editMessage]);

    const handleDrawerOpen = () => {
        setOpen(true);
    };

    const handleDrawerClose = () => {
        setOpen(false);
    };

    if (!chat)
        return (<Skeleton/>)

    if (!me)
        return (<Skeleton/>)

    if (editMessageId && !editMessage)
        return (<Skeleton/>)

    const sendMessage = (msg: string) => {
        if (msg.trim() !== "") {
            if (chat)
                if (editMessageId) {
                    replaceTextMessage(editMessageId, {
                        content: msg,
                    }).then(message => {
                        setEditMessageId(undefined)
                    })
                } else
                    createTextMessage(chat.id, {
                        content: msg,
                    }).then(message => {
                    })

            setText("")
        }
    };

    function handleExit() {
        leaveRoom(chatId).then(chat => window.location.reload())
    }

    function handleCopyJoinLink() {
        if (chat)
            navigator.clipboard.writeText(getJoinLink(chat))
    }

    function handleChangeFile(file: SetStateAction<File>) {
        setFile(file as SetStateAction<File | undefined>)
    }

    if (file) {
        uploadFile(file).then(url => {
            createImageMessage(chat.id, {
                url: url
            }).then(message => {
            })
        })
        setFile(undefined)
    }

    return (
        <Box sx={{display: 'grid'}}>
            <AppBar position="sticky" sx={{top: '64px', zIndex: (theme) => theme.zIndex.drawer - 1}}>
                <Toolbar>
                    <Typography variant="h6" component="div" sx={{flexGrow: 1}}>
                        {chatTitle}
                    </Typography>
                    <IconButton
                        color="inherit"
                        aria-label="open drawer"
                        onClick={handleDrawerOpen}
                        edge="start"
                        sx={{mr: 2, ...(open && {display: 'none'})}}
                    >
                        <MenuIcon/>
                    </IconButton>
                </Toolbar>
            </AppBar>
            <Drawer
                anchor="right"
                open={open}
                onClose={handleDrawerClose}
            >
                <Box sx={{width: 250}} role="presentation" onClick={handleDrawerClose}>
                    <Toolbar/>
                    <Divider/>
                    <Box sx={{overflow: 'auto'}}>
                        <ChatUsers chatId={chatId}/>
                    </Box>
                    <Divider/>
                    <HideIf hide={chat.type == 'contact'}>
                        <Box sx={{m: 1}}>
                            <Button onClick={handleCopyJoinLink}>
                                Copy join link
                            </Button>
                        </Box>
                    </HideIf>
                    <Box sx={{m: 1}}>
                        <Button color='warning' onClick={handleExit}>
                            Exit
                        </Button>
                    </Box>
                </Box>
            </Drawer>
            <Messages chatId={chatId} editMessageId={editMessageId}
                      setEditMessageId={setEditMessageId}/>
            <HideIf hide={!canWrite}>
                <AppBar position="sticky" color='inherit'
                        sx={{bottom: '0px', zIndex: (theme) => theme.zIndex.drawer - 1}}>
                    <Toolbar>
                        <Typography variant="h6" component="div" sx={{
                            flexGrow: 1,
                        }}>
                            <DragDrop handleChangeFile={handleChangeFile}>
                                <textarea
                                    name="user_input"
                                    placeholder={
                                        "Напишите свое сообщение..."
                                    }
                                    value={text}
                                    onChange={(event) => setText(event.target.value)}
                                    onKeyPress={(event) => {
                                        if (event.key === 'Enter')
                                            sendMessage(text);
                                    }}
                                    style={{
                                        border: 'none',
                                        width: '100%',
                                    }}
                                />
                            </DragDrop>
                        </Typography>
                        <IconButton
                            color="primary"
                            aria-label="send message"
                            onClick={() => {
                                sendMessage(text);
                            }}
                            edge="start"
                            sx={{
                                ml: 2,
                            }}
                        >
                            <Send/>
                        </IconButton>
                    </Toolbar>
                </AppBar>
            </HideIf>
        </Box>
    );
}

function getRole(attachment: UserRoleAttachment): string {
    if ('role' in attachment)
        switch (attachment.role) {
            case UserChanelRoleAttachmentRole.PUBLIC_USER:
                return "USER"
            default:
                return attachment.role
        }
    return ""
}

function ChatUsers({
                       chatId
                   }: {
    chatId: number
}) {
    const attachments = useRoomUsers(chatId)

    return (
        <List>
            {
                attachments
                    ?
                    attachments.map(attachment =>
                        <ListItem key={attachment.user}>
                            <UserIdInfo id={attachment.user}>
                                <ListItemAvatar>
                                    <UserAvatar/>
                                </ListItemAvatar>
                                <ListItemText primary={<UserName/>} secondary={getRole(attachment)}/>
                            </UserIdInfo>
                        </ListItem>
                    )
                    : <Skeleton/>
            }
        </List>
    )
}


function getJoinLink(chat: Room) {
    return `${CLIENT_URI}/join/${chat.id}`;
}