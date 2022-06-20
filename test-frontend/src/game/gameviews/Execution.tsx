import Players, { PlayerList } from '../players/Players';

export const ExecutionView = (props: { players: PlayerList }) => {
	return <Players players={props.players} />;
};
