import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;


class Phone {                               //클래스 Phone 여기서 거의 다 해결한다.
    Scanner sc =new Scanner(System.in);
    String id = "root";                     //데이터베이스 접근 아이디
    String password = "1234";               //데이터베이스 root에 대한 비밀번호
    Connection conn;
    PreparedStatement pstmt;
    String name,address,phoneNumber;        //데이터베이스에 넘길 이름,주소,전화번호를 변수선언
    ArrayList<String> temp= new ArrayList<>();      //name 중복 계산하기 위한 ArrayList
    Phone() throws SQLException {
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/phone", id, password);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    boolean nameCheck(String s) throws SQLException {
        String sql="select name from phone";                    //phone테이블에 name 쿼리문
        pstmt=conn.prepareStatement(sql);
        ResultSet rs=pstmt.executeQuery();
        while (rs.next()){
            temp.add(rs.getString("name"));         //ArrayList 에 데이터베이스 네임 추가
        }
        for(int i=0;i<temp.size();i++){
            if(temp.get(i).equals(s)){
                return true;                                    //입력받은 s와 temp 비교하며 같으면 트루
            }
        }
        return false;
    }


    void dataInput() throws InterruptedException, SQLException, UnsupportedEncodingException {

        String sql="select name from phone";                                //폰 테이블 네임 선택 쿼리문
        pstmt=conn.prepareStatement(sql);

        System.out.println("1.추가");
        System.out.print("이름 :");  setName(sc.nextLine());                  //입력받은 값들을 set으로 변수대입
        System.out.print("전화번호 :"); setPhoneNumber(sc.nextLine());
        System.out.print("주소 :");   setAddress(sc.nextLine());

        if(nameCheck(getName())){                                           //nameCheck(입력받은이름) 메소드에서 트루이면 실행
            System.out.println("이미 등록된 이름입니다.");
            return;
        }
        String charset  = "euc-kr";     //mysql과 같은 바이트 크기의 유니코드        //데이터베이스 바이트가 틀리면 에러를 띄우므로 인터넷 검색해서 찾았다.
        int byteName=getName().getBytes(charset).length;    // 이름의 바이트 계산
        int byteNum=getPhoneNumber().getBytes(charset).length;
        int byteAdd=getName().getBytes(charset).length;

        if(8<byteName){         //데이터베이스에 설정한 바이트수 값이 넘어가면...
            System.out.println("입력하신 이름의 길이가 너무 깁니다.");
            return;
        }
        if(45<byteNum){
            System.out.println("입력하신 번호의 길이가 너무 깁니다.");
            return;
        }
        if(45<byteAdd){
            System.out.println("입력하신 주소의 길이가 너무 깁니다.");
            return;
        }


        try {
            pstmt = conn.prepareStatement("insert into phone values (?,?,?);");
            pstmt.setString(1, getName());
            pstmt.setString(2, getPhoneNumber());
            pstmt.setString(3, getAddress());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("입력 되었습니다.");
    }
    void serching() throws SQLException {

        String name;
        System.out.println("2.이름 검색");
        System.out.print("이름 :");
        name= sc.nextLine();

        if(!nameCheck(name)){
            System.out.println("전화번호부에 없습니다.");
            return;
        }


        String sql="select * from phone where name=?";
        pstmt=conn.prepareStatement(sql);
        pstmt.setString(1,name);
        ResultSet rs=pstmt.executeQuery();


        while (rs.next()){
            System.out.println("name\tphoneNumber\t\taddress");
            System.out.println("---------------------------");
            System.out.print(rs.getString("name")+"\t");
            System.out.print(rs.getString("phoneNumber")+"\t");
            System.out.print(rs.getString("address")+"\t");
            System.out.println();
            return;
        }
    }

    void del() throws SQLException {
        System.out.println("3.삭제");
        System.out.print("이름 :");
        String name=sc.next();

        if(!nameCheck(name)){
            System.out.println("전화번호부에 없습니다.");
            return;
        }

        String sql="delete from phone where name=?";
        pstmt=conn.prepareStatement(sql);
        pstmt.setString(1,name);
        pstmt.executeUpdate();
        System.out.println("선택하신 목록이 지워졌습니다.");
    }

    void selectAll() throws SQLException {
        String sql="select * from phone;";
        pstmt=conn.prepareStatement(sql);
        ResultSet rs=pstmt.executeQuery();
        System.out.println("name\tphoneNumber\t\taddress");
        System.out.println("---------------------------");

        while (rs.next()){
            System.out.print(rs.getString("name")+"\t");
            System.out.print(rs.getString("phoneNumber")+"\t");
            System.out.print(rs.getString("address")+"\t");
            System.out.println();
        }
    }
    void allDelete() throws SQLException {
        String sql="truncate table phone;";
        pstmt=conn.prepareStatement(sql);
        pstmt.executeUpdate();
    }
}
public class TelNumber {
    public static void main(String[] args) throws SQLException, InterruptedException, UnsupportedEncodingException {
        Scanner sc = new Scanner(System.in);
        Phone phone = new Phone();
        phone.allDelete();// 테이블내 전체 삭제문 정리가 안되서 넣었다.
        System.out.println("전화번호부 만들기 - 데이터베이스 연동");
        while (true){
            System.out.println("1.입력 2.검색 3.삭제 4.전체출력 5.종료");
            String choice = sc.nextLine();              // int로 받으면 글씨 입력시 에러를 발생하여 문자열로 받았다.

            if(choice.equals("1")){
                phone.dataInput();
            }
            else if(choice.equals("2")){
                phone.serching();

            }
            else if (choice.equals("3")) {
                phone.del();

            }
            else if (choice.equals("4")) {
                phone.selectAll();
            }
            else if(choice.equals("5")){
                System.out.println("프로그램을 종료합니다.");
                System.exit(1);
            }
            else {
                System.out.println("잘못 누르셨습니다.");
            }
        }
    }
}
