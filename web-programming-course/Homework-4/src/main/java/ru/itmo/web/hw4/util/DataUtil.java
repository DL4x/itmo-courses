package ru.itmo.web.hw4.util;

import ru.itmo.web.hw4.model.Post;
import ru.itmo.web.hw4.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DataUtil {
    private static final List<User> USERS = Arrays.asList(
            new User(1, "MikeMirzayanov", "Mike Mirzayanov", Color.BLUE),
            new User(6, "pashka", "Pavel Mavrin", Color.RED),
            new User(9, "geranazavr555", "Georgiy Nazarov", Color.GREEN),
            new User(11, "tourist", "Gennady Korotkevich", Color.RED)
    );

    private static final List<Post> POSTS = Arrays.asList(
            new Post(1, "My first post!", "Country form amiable distant dine described its vexed sensible length address looking abode instantly immediate forth. \n" +
                    "\n" +
                    "Fond are hearted bred wishes pressed post opinions sitting. Believing wish something dearest proposal. Unfeeling your wholly frequently drift insipidity disposing fanny itself. Sentiments civilly invitation latter observe. This mistake forty likewise attacks too address desirous. \n" +
                    "\n" +
                    "Are resolving settle hastened learning everything not advantage forming wandered. Procuring books formerly twenty sons or remaining than expenses however saved very precaution exposed. Besides demesne garden daughter son removing astonished. Handsome marriage discourse estate like settling power letter afford margaret no chiefly cousin yourself talked greater wound. Taste busy instrument fine removing herself dare song would removed justice moreover unable and pressed arise. \n" +
                    "\n" +
                    "Justice fanny acuteness see examine painful unfeeling declared hopes dwelling. Fruit removed scarcely. Shewing extremity relied simple debating desire against unfeeling peculiar resolved subjects. Exercise view dissuade stairs know burst too. Sixteen one prevent wise unpleasing indeed propriety shall delicate instrument. \n" +
                    "\n" +
                    "Enough say on indulged replied adieus so  drawn concealed sure raising see having up. Inquietude pasture conveying musical are. Securing fulfilled trees. Parlors thing indulgence sent thoughts fact address temper while may course. Dinner furniture behaviour end chief sure blind landlord noisy among edward quiet show mention tore remain. \n" +
                    "\n" +
                    "Were learn forty sir. Drift throwing everything mistaken delight feel. Studied praise pronounce be tried preference plan last together widen this rooms son rooms. Hope greater own walls placing repeated have excellent must enabled blind winding itself remainder fruit incommode. Regular something luckily shortly discovery course lady. \n" +
                    "\n" +
                    "Ladyship present state affixed winter itself.", 1),
            new Post(2, "Minutes", "Minutes certainly me blessing ever evil john evil excuse there entered preference late figure. \n" +
                    "\n" +
                    "Simple unknown projecting green sweetness life through. Pleasure who education large preference breakfast times boy denoting consisted likewise placing perceive than yet or. Folly consisted over rich confined wished horrible reserved mistaken period stairs latter ten was john dare be. In wrote see pianoforte know county convinced waited related passed. Result then open smart happen eyes that. \n" +
                    "\n" +
                    "Along estimable moderate supposing sold warrant voice outweigh opinions continual dinner around something regret twenty. Promotion means snug landlord shutters resources family scale happiness. Miss built out away day jokes demands exposed time timed. Concern carried moment towards twenty sake. There resembled fail however highly account pleased must occasional assistance attended unlocked sudden piqued improve possible. \n" +
                    "\n" +
                    "Other sell its civilly. Otherwise lasting married bachelor reserved delighted rent sister ought merry cordial period decisively excuse small prepared listening. Confined me and prudent moderate lived immediate sold confined.", 9),
            new Post(3, "Some text", "Innate margaret felicity feet game acceptance admiration insensible cultivated indeed gave maids hoped. \n" +
                    "\n" +
                    "Subject replying domestic held. Honoured projecting full figure place. Tell delightful often formed square rent. Sixteen apartments months. Quiet does objection really either sudden consulted shortly voice thoughts dashwoods weeks inquietude tall under has. \n" +
                    "\n" +
                    "Sending add outweigh remainder luckily supported attachment. House sussex off their hoped absolute excellence match removed avoid hopes pursuit breakfast they. Provision distance simple has concealed played marriage elderly sweetness spring done pronounce ready. None new consisted. Newspaper leave removal. \n" +
                    "\n" +
                    "Believed conviction so admire regular pursuit been jointure terms with desire afford preserved from attachment piqued. Absolute fanny vicinity life thirty view those esteems temper sudden genius next ecstatic. Although your repair size attempt oppose towards resolve favour. Admiration ourselves child up amounted having. Reached determine power offered meet. \n" +
                    "\n" +
                    "Discourse uneasy promise witty improving hills occasional stand past husbands upon therefore decisively distant husbands. Home time agreeable early extensive produced natural handsome. Denoting conviction those. Forfeited behaved another announcing outlived two deficient was excellence anxious explain mean. Promotion september collecting building spot rest there gay. \n" +
                    "\n" +
                    "Procuring suppose subject giving pronounce moment rapturous twenty above humanity moonlight. Middleton nothing wandered future. Hastened colonel departure appearance hastily pianoforte travelling affection. Particular eyes outlived about pressed remarkably forth for assistance prosperous. Regard elderly may change grave assurance been delightful far men beloved feet unpacked consisted. \n" +
                    "\n" +
                    "With motionless society suffering end. Delivered cease invitation smiling unsatiable doors met no talking. Education outward sight say seen prepared comparison affronting hills interest children be morning maids tended half. Middletons unsatiable ham. Commanded court admiration both diverted praise interest passage confined compliment sufficient produce men. \n" +
                    "\n" +
                    "Welcome friendly total blush distrusts. Outlived civil too walk improved true marked looked small dwelling jokes. Even companions from can sight cousin strongly astonished absolute seven soon rent explain delightful improved song inhabit. Your forfeited coming three applauded wise. Ladyship terminated screened. \n" +
                    "\n" +
                    "Resolve truth horrible pleasure windows charm followed. Smallest imprudence stimulated instantly. Excited consider insensible cold shameless between vicinity painful sincerity opinion request arranging. Replied windows court visited discourse. Scale attachment remaining ladies know propriety performed address. \n" +
                    "\n" +
                    "Taken no around convinced outward waiting instrument. Reserved visitor forth provided than mr projecting ferrars suffer even distance. Offence was ye forming unable was difficult themselves affixed woody prospect ability his widen. Apartments friend balls. Latter females sent settling worth law come  brought husband window whole sometimes conduct front. \n" +
                    "\n" +
                    "Principle its pretended delightful offending spoil together written rapturous.", 6),
            new Post(4, "Solicitude placing quitting...", "Solicitude placing quitting so strangers doubtful civility when whole in were felt better applauded ye. \n" +
                    "\n" +
                    "Fine right mind prepare colonel reasonably waiting reasonably. Advanced asked mistake cordial thoughts announcing offence seeing edward dine. Civilly solid speedily who examine affixed principle who defective merely power formerly post. Fortune declared equally cease express size thought judgment certain offered assured truth were course only inquietude. Son raptures returned years pressed call general. \n" +
                    "\n" +
                    "Engaged chamber dearest put of ask seen. Formed  worth coming depend general departure visit one shortly existence fancy. Bringing sir building would fail. Prudent dearest feeling. Announcing basket wishing summer anxious past innate. \n" +
                    "\n" +
                    "Ten improved improved society celebrated nothing. All matters grave denoting length anxious period. Juvenile rent at incommode additions amounted partiality conduct life. Estimable extent name opinion nay.", 9)
    );

    public static void addData(HttpServletRequest request, Map<String, Object> data) {
        data.put("users", USERS);
        data.put("posts", POSTS);

        for (User user : USERS) {
            if (Long.toString(user.getId()).equals(request.getParameter("logged_user_id"))) {
                data.put("user", user);
            }
        }
    }
}
