package com.dbboys.customnode;

import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

//巡检结论一列自定义格式
public class CustomCheckTableCell<S,T> extends CustomTableCell<S,T> {


    SVGPath icon = new SVGPath();
    SVGPath icon1 = new SVGPath();
    SVGPath icon2 = new SVGPath();
    Group group=new Group(icon);
    Group group1=new Group(icon1);
    Group group2=new Group(icon2);

    public CustomCheckTableCell(){
        icon.setScaleX(0.65);
        icon.setScaleY(0.65);
        icon.setContent("M17.7656 5.8594 Q18 6.0938 17.7656 6.375 L7.9688 16.1719 Q7.8281 16.3125 7.6875 16.3125 L7.6875 16.3125 Q7.5469 16.3125 7.4062 16.1719 L3.6094 12.4219 Q3.4688 12.2812 3.4688 12.1406 Q3.4688 12 3.6094 11.8594 L5.6719 9.7969 Q5.9531 9.5156 6.2344 9.7969 L7.6875 11.25 L15.1406 3.75 Q15.2344 3.6562 15.4219 3.6562 Q15.6094 3.6562 15.7031 3.75 L17.7656 5.8594 ZM12.9844 12.8906 L15.375 10.4531 L15.375 19.125 Q15.375 19.5938 15 19.9688 Q14.625 20.3438 14.1562 20.3438 L14.1094 20.3438 L14.1094 20.3438 L1.2188 20.3438 L1.2188 20.3438 L1.2188 20.3438 Q0.75 20.3438 0.375 19.9688 Q0 19.5938 0 19.125 L0 6.1875 Q0 5.7188 0.375 5.3438 Q0.75 4.9688 1.2188 4.9688 L1.2656 4.9688 L12.2344 4.9688 L9.8438 7.3594 L2.3906 7.3594 L2.3906 17.9531 L12.9844 17.9531 L12.9844 12.8906 Z");
        icon.setFill(Color.valueOf("#074675"));

        icon1.setScaleX(0.5);
        icon1.setScaleY(0.5);
        icon1.setContent("M13.7188 19.2734 L13.7188 16.7266 Q13.7188 16.5391 13.5781 16.4141 Q13.4531 16.2891 13.2812 16.2891 L10.7188 16.2891 Q10.5469 16.2891 10.4062 16.4141 Q10.2812 16.5391 10.2812 16.7266 L10.2812 19.2734 Q10.2812 19.4609 10.4062 19.5859 Q10.5469 19.7109 10.7188 19.7109 L13.2812 19.7109 Q13.4531 19.7109 13.5781 19.5859 Q13.7188 19.4609 13.7188 19.2734 ZM13.6875 14.2578 L13.9219 8.1172 Q13.9219 7.9609 13.7969 7.8672 Q13.625 7.7109 13.4688 7.7109 L10.5312 7.7109 Q10.375 7.7109 10.2031 7.8672 Q10.0781 7.9609 10.0781 8.1484 L10.2969 14.2578 Q10.2969 14.3984 10.4219 14.4922 Q10.5625 14.5703 10.75 14.5703 L13.2344 14.5703 Q13.4219 14.5703 13.5469 14.4922 Q13.6719 14.3984 13.6875 14.2578 ZM13.5 1.7578 L23.7812 20.6172 Q24.25 21.4609 23.7656 22.3047 Q23.5312 22.6953 23.125 22.9141 Q22.7344 23.1484 22.2812 23.1484 L1.7188 23.1484 Q1.2656 23.1484 0.8594 22.9141 Q0.4688 22.6953 0.2344 22.3047 Q-0.25 21.4609 0.2188 20.6172 L10.5 1.7578 Q10.7344 1.3359 11.125 1.1016 Q11.5312 0.8516 12 0.8516 Q12.4688 0.8516 12.8594 1.1016 Q13.2656 1.3359 13.5 1.7578 Z");
        icon1.setFill(Color.valueOf("#ffbf00"));

        icon2.setScaleX(0.05);
        icon2.setScaleY(0.05);
        icon2.setContent("M0,170l65.555-65.555L0,38.891L38.891,0l65.555,65.555L170,0l38.891,38.891l-65.555,65.555L208.891,170L170,208.891 l-65.555-65.555l-65.555,65.555L0,170z");
        icon2.setFill(Color.valueOf("#cf2311"));
    }


    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);
        if (empty) {
        } else if(item==null){

        }else{
            if(item.equals("0")){
                setGraphic(group);
                setText("正常");
            }else if(item.equals("1")){
                setGraphic(group1);
                setText("关注");
            }else{
                setGraphic(group2);
                setText("异常");
            }
        }

    }



}
